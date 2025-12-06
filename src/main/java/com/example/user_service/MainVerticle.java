package com.example.user_service;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;

public class MainVerticle extends VerticleBase {

  @Override
  public Future<?> start() {
    return vertx.createHttpServer()
      .requestHandler(req -> {
        req.response()
          .putHeader("content-type", "text/plain")
          .end("Hello from Vert.x!");
      })
      .listen(8888)                       // <--- changed port here
      .onSuccess(http -> {
        System.out.println("HTTP server started on port 8888");
      });
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle())
      .onSuccess(id -> System.out.println("MainVerticle deployed: " + id))
      .onFailure(Throwable::printStackTrace);
  }
}
