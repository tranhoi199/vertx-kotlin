package com.example.technology

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle(private val router: Router) : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    router.route().handler(BodyHandler.create())
    router.get("/api/v1/user/").handler(this::helloVertx)
    router.get("/api/v1/user/:name").handler(this::helloWithHame)
    router.post("/api/v1/user/add").handler(this::createUser)
  }

  private fun helloVertx(ctx: RoutingContext) {
    vertx.eventBus().request<String>("user.vertx.addr", "") {
      ctx.request().response().end(it.result().body().toString())
    }
  }

  private fun helloWithHame(ctx: RoutingContext) {
    val name = ctx.pathParam("name")
    vertx.eventBus().request<String>("user.name.addr", name) {
       ctx.request().response().end(it.result().body().toString())
    }
  }

  private fun createUser(ctx: RoutingContext) {
    val bodyAsString = ctx.bodyAsString
    vertx.eventBus().request<String>("user.create.addr", bodyAsString) {
      ctx.request().response().end(it.result().body().toString())
    }
  }
}
