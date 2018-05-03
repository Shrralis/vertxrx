package com.shrralis.blog;

import io.reactivex.Single;
import io.vertx.reactivex.core.RxHelper;
import io.vertx.reactivex.core.Vertx;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Starter {

    public static void main(String[] args) {
        Single<String> deployment = RxHelper.deployVerticle(Vertx.vertx(), new ShrralisBlogVerticle());

        deployment.subscribe(
            id -> LOGGER.info("SUCCESS with deploying ShrralisBlogVerticle"),
            err -> LOGGER.error("ERROR with deploying ShrralisBlogVerticle"));
    }
}
