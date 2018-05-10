package com.shrralis.blog.resource.core;

import io.reactivex.observers.DefaultObserver;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.Router;
import io.vertx.reactivex.ext.web.RoutingContext;
import lombok.AllArgsConstructor;
import lombok.val;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.shrralis.blog.resource.core.ApiResource.Subscription.answer;

public interface ApiResource {

    default String path() {
        val clazz = getClass();

        if (clazz.isAnnotationPresent(ApiPath.class)) {
            return clazz.getAnnotation(ApiPath.class).value();
        }
        return null;
    }

    @AllArgsConstructor
    class Subscription<T> extends DefaultObserver<T> {

        private final RoutingContext context;

        @Override
        public void onNext(T response) {
            answer(context, () -> ResponseContainer.builder().payload(response).build());
        }

        @Override
        public void onError(Throwable e) {
            fail(context, e);
        }

        @Override
        public void onComplete() {
            // NOSONAR noop;
            // add rate limiter gere
        }

        static <T> void answer(RoutingContext context, Supplier<ResponseContainer<T>> responseOf) {
            try {
                val result = responseOf.get();

                if (result == null) {
                    context.response().setStatusCode(404).end();
                } else if (result.isValid()) {
                    context.response().setStatusCode(200).end(result.asString());
                } else {
                    context.response().setStatusCode(500).end(result.asString());
                }
            } catch (Exception e) {
                context.response().end(Json.encodePrettily(ApiException.of(e).toMap()));
            }
        }

        static void fail(RoutingContext context, Throwable e) {
            answer(context, () -> ResponseContainer.builder()
                .errors(e instanceof ApiException ? ((ApiException) e).toMap() : ApiException.of(e).toMap())
                .build());
        }
    }

    default void mount(Router router, Vertx vertx) {
        throw new IllegalStateException(String.format("The operation should be overridden in %s", getClass()));
    }

    default void fail(RoutingContext context, Throwable ex) {
        Subscription.fail(context, ex);
    }

    default <E> Consumer<RoutingContext> on(E arg) {
        BiConsumer<RoutingContext, E> fn = arg instanceof Throwable
            ? (context, obj) -> fail(context, (Throwable) arg)
            : arg instanceof ResponseContainer // NOSONAR;
            ? (context, obj) -> answer(context, () -> (ResponseContainer<?>) arg)
            : (context, obj) -> answer(context, () -> ResponseContainer.builder().payload(arg).build());
        Function<E, Consumer<RoutingContext>> currier = obj -> rc -> fn.accept(rc, obj);

        return currier.apply(arg);
    }
}
