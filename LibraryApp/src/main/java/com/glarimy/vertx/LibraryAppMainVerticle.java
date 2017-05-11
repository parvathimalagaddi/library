package com.glarimy.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class LibraryAppMainVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> future) throws Exception {
        Router router = Router.router(vertx);

        vertx.deployVerticle("com.glarimy.vertx.MongoServiceVerticle",
                new DeploymentOptions().setWorker(true));

        router.route("/static/*").handler(StaticHandler.create("web"));

        router.get("/library/book/:isbn").handler(rctx -> {
            vertx.eventBus().send("com.glarimy.vertx.library.book.find.isbn",
                    rctx.request().getParam("isbn"), res -> {
                        System.out.println("Result for Query by ISBN "
                                + res.result().body());

                        if (res.result().body().toString().isEmpty()) {
                            rctx.response().setStatusCode(404).end();
                        } else {
                            rctx.response().setStatusCode(200)
                                    .putHeader("Content-Type",
                                            "application/json")
                                    .end(res.result().body().toString());
                        }
                    });
        });

        router.route("/library/book").handler(BodyHandler.create());
        router.post("/library/book").handler(rctx -> {
            vertx.eventBus().send("com.glarimy.vertx.library.book.add",
                    rctx.getBodyAsJson().encodePrettily(), res -> {

                        if (res.result().body().toString().equals("-1")) {
                            rctx.response().setStatusCode(500).end();
                        } else {

                            rctx.response().setStatusCode(201)
                                    .putHeader("Content-Type",
                                            "application/json")
                                    .putHeader("Location", "library/book/"
                                            + res.result().body().toString())
                                    .end();
                        }
                    });

        });

        vertx.createHttpServer().requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080), result -> {
                    if (result.succeeded()) {
                        future.complete();
                    } else {
                        future.fail(result.cause());
                    }
                });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}