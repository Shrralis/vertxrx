package com.shrralis.blog;

import io.vertx.core.Verticle;
import io.vertx.core.spi.VerticleFactory;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.inject.Named;

@Slf4j
@Named("shrralis-factory")
public class ShrralisBlogFactory implements VerticleFactory, ApplicationContextAware {

    private static final String FACTORY_PREFIX = "shrralis";

    private ApplicationContext context;

    /**
     * Returns the verticleClass identifier with the hk2 prefix for deployment
     *
     * @param clazz the class of deployed verticleClass
     * @return the identifier
     */
    public static String verticleClass(Class<? extends Verticle> clazz) {
        return verticleClass(clazz.getName());
    }

    /**
     * Returns the verticleClass identifier with the hk2 prefix for deployment
     *
     * @param className the class name of deployed verticleClass
     * @return the identifier
     */
    public static String verticleClass(String className) {
        return String.format("%s:%s", FACTORY_PREFIX, className);
    }

    /**
     * Usually verticleClass instantiation is fast, but since Shrralis verticles are Spring Beans,
     * they might depend on other beans/resources which are slow to build/lookup.
     *
     * @return always {@literal true}
     * @since 1.0.0
     */
    @Override
    public boolean blockingCreate() {
        return true;
    }

    /**
     * Returns arbitrary string which is uniquely identify the verticleClass factory.
     *
     * @return the identity prefix of factory
     * @since 1.0.0
     */
    @Override
    public String prefix() {
        return FACTORY_PREFIX;
    }

    /**
     * Creates an instance of verticleClass by prefixed class name of latter.
     *
     * @param verticleName the prefixed class name of verticleClass instance
     * @param classLoader  the current isolated classloader
     * @return the new instance of verticleClass created by spring factory.
     *
     * @throws Exception any kind of exception raised while verticleClass is being instantiated.
     */
    @Override
    public Verticle createVerticle(String verticleName, ClassLoader classLoader) throws Exception {
        val className = VerticleFactory.removePrefix(verticleName);

        if (className.startsWith("bean")) {
            val beanName = VerticleFactory.removePrefix(className);
            val bean = context.getBean(beanName);

            LOGGER.info("Starting Shrralis bean module: {} ", beanName);
            return Verticle.class.cast(bean);
        } else {
            val bean = context.getBean(Class.forName(className, true, classLoader));

            LOGGER.info("Starting Shrralis class module: {} ", className);
            return Verticle.class.cast(bean);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
