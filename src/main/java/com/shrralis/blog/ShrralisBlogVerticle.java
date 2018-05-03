package com.shrralis.blog;

import io.vertx.core.Future;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.http.HttpServer;
import io.vertx.reactivex.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class ShrralisBlogVerticle extends AbstractVerticle {

    private static final String ANY_PATH = "/*";

    private HttpServer server;

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
