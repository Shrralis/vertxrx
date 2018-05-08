package com.shrralis.blog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan("com.shrralis.**")
public class ShrralisBlog extends AbstractVerticle {

    private static final String ANY_PATH = "/*";

    private HttpServer server;

    static {
        System.setProperty("vertx.logger-delegate-factory-class-name", "io.vertx.core.logging.SLF4JLogDelegateFactory");
        Json.mapper
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module());
        Json.prettyMapper
                .registerModule(new JavaTimeModule())
                .registerModule(new Jdk8Module())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Override
    public void start(Future<Void> startup) {
        LOGGER.info("Vertx started");
        LOGGER.info("Starting server...");

        val router = Router.router(vertx);
        val port = config().getInteger("http.port", 8000);

        router.get("/").handler(req -> req.response().end("You are running ShrralisBlog"));

        server = vertx.createHttpServer().requestHandler(router::accept).listen(port, result -> {
            if (result.succeeded()) {
                LOGGER.info("Started ShrralisBlog API on port: {}", port);
                startup.complete();
            } else {
                LOGGER.error("ShrralisBlog API failed on starting up with cause: {}", result.cause());
                startup.fail(result.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> closer) {
        LOGGER.info("Shutting down application...");
        server.close(result -> {
            if (result.succeeded()) {
                closer.complete();
            } else {
                closer.fail(result.cause());
            }
        });
    }
}
