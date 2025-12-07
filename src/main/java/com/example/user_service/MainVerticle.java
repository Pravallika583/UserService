package com.example.user_service;

import com.example.user_service.users.UserServiceImpl;
import com.example.user_service.users.User;
import com.example.user_service.users.UserNotFoundException;
import com.example.user_service.users.UserService;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.core.json.JsonObject;

public class MainVerticle extends VerticleBase {

  // Our in-memory, thread-safe service
  private final UserService userService = new UserServiceImpl();

  @Override
  public Future<?> start() {
    // Create router
    Router router = Router.router(vertx);
    router.route().handler(io.vertx.ext.web.handler.BodyHandler.create());

    // Routes
    router.post("/users").handler(this::handleCreateUser);
    router.get("/users/:id").handler(this::handleGetUserById);
    router.put("/users/:id/email").handler(this::handleUpdateUserEmail);
    router.delete("/users/:id").handler(this::handleDeleteUser);

    // Start HTTP server on port 8888
    return vertx
      .createHttpServer()
      .requestHandler(router)
      .listen(8888)
      .onSuccess(server ->
        System.out.println("HTTP server started on port " + server.actualPort())
      );
  }

  // ---------- Handlers ----------

  // POST /users
  // Body: { "name": "Kelly", "email": "kelly@gmail.com" }
  private void handleCreateUser(RoutingContext ctx) {
    JsonObject body = ctx.body().asJsonObject();
    if (body == null) {
      badRequest(ctx, "Request body must be JSON");
      return;
    }

    String name = body.getString("name");
    String email = body.getString("email");

    if (name == null || name.isBlank() || email == null || email.isBlank()) {
      badRequest(ctx, "Both 'name' and 'email' are required");
      return;
    }

    User user = userService.createUser(name, email);
    JsonObject responseJson = toJson(user);

    ctx.response()
      .setStatusCode(201)
      .putHeader("content-type", "application/json")
      .end(responseJson.encode());
  }

  // GET /users/:id
  private void handleGetUserById(RoutingContext ctx) {
    Long id = parseIdPathParam(ctx);
    if (id == null) {
      return;
    }

    try {
      User user = userService.getUser(id);
      JsonObject json = toJson(user);
      ctx.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(json.encode());
    } catch (UserNotFoundException e) {
      notFound(ctx, e.getMessage());
    }
  }

  // PUT /users/:id/email
  // Body: { "email": "new-email@example.com" }
  private void handleUpdateUserEmail(RoutingContext ctx) {
    Long id = parseIdPathParam(ctx);
    if (id == null) {
      return;
    }

    JsonObject body = ctx.body().asJsonObject();
    if (body == null) {
      badRequest(ctx, "Request body must be JSON");
      return;
    }

    String newEmail = body.getString("email");
    if (newEmail == null || newEmail.isBlank()) {
      badRequest(ctx, "'email' field is required");
      return;
    }

    try {
      User updated = userService.updateUserEmail(id, newEmail);
      JsonObject json = toJson(updated);
      ctx.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json")
        .end(json.encode());
    } catch (UserNotFoundException e) {
      notFound(ctx, e.getMessage());
    }
  }

  // DELETE /users/:id
  private void handleDeleteUser(RoutingContext ctx) {
    Long id = parseIdPathParam(ctx);
    if (id == null) {
      return;
    }

    try {
      userService.deleteUser(id);
      ctx.response()
        .setStatusCode(200) // No Content
        .putHeader("content-type", "application/json")
        .end(new JsonObject()
          .put("message", "User " + id + " left")
          .encode()
        );
    } catch (UserNotFoundException e) {
      notFound(ctx, e.getMessage());
    }
  }

  // ---------- Helper methods ----------

  private Long parseIdPathParam(RoutingContext ctx) {
    String idStr = ctx.pathParam("id");
    if (idStr == null) {
      badRequest(ctx, "Path param 'id' is required");
      return null;
    }
    try {
      return Long.parseLong(idStr);
    } catch (NumberFormatException e) {
      badRequest(ctx, "Invalid id: " + idStr);
      return null;
    }
  }

  private JsonObject toJson(User user) {
    return new JsonObject()
      .put("id", user.getId())
      .put("name", user.getName())
      .put("email", user.getEmail());
  }

  private void badRequest(RoutingContext ctx, String message) {
    ctx.response()
      .setStatusCode(400)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", message).encode());
  }

  private void notFound(RoutingContext ctx, String message) {
    ctx.response()
      .setStatusCode(404)
      .putHeader("content-type", "application/json")
      .end(new JsonObject().put("error", message).encode());
  }
}
