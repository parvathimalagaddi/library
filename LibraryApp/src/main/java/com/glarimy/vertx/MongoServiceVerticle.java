package com.glarimy.vertx;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoServiceVerticle extends AbstractVerticle {
    @Override
    public void start() throws Exception {
        vertx.eventBus().consumer("com.glarimy.vertx.library.book.find.isbn",
                message -> {

                    JsonObject config = new JsonObject();
                    config.put("db_name", "library");
                    config.put("connection_string",
                            "mongodb://mongoinstance:27017");
                    MongoClient client = MongoClient.createShared(vertx,
                            config);

                    client.find("books", new JsonObject().put("isbn",
                            message.body().toString()), res -> {
                                if (res.succeeded()) {
                                    if (res.result().size() != 0) {
                                        System.out.println("Available Books " + res.result());
                                        message.reply(res.result().toString());
                                    } else {
                                        message.reply("");
                                    }
                                } else {
                                    res.cause().printStackTrace();
                                    message.reply("");
                                }
                            });
                });

        vertx.eventBus().consumer("com.glarimy.vertx.library.book.add",
                message -> {

                    JsonObject config = new JsonObject();
                    config.put("db_name", "library");
                    config.put("connection_string",
                            "mongodb://mongoinstance:27017");
                    MongoClient client = MongoClient.createShared(vertx,
                            config);
                    System.out.println("To be Created Book " +message.body().toString());
                    client.insert("books",
                            new JsonObject(message.body().toString()), res -> {
                                if (res.succeeded()) {
                                    message.reply(res.result());
                                } else {
                                    res.cause().printStackTrace();
                                    message.reply(-1);
                                }

                            });
                });
    }
}
