package com.example.user_service;

import io.vertx.core.Vertx;

public class Main {

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();

    vertx.deployVerticle(new MainVerticle())
      .onSuccess(id ->
        System.out.println("MainVerticle deployed with id: " + id)
      )
      .onFailure(Throwable::printStackTrace);
  }
}
