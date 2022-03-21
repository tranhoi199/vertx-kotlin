package com.example.technology

import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.AbstractVerticle
import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.Promise
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler

class MainVerticle : AbstractVerticle() {

  override fun start(startPromise: Promise<Void>) {
    vertx.deployVerticle(HelloVerticle())

    var router: Router = Router.router(vertx)

    router.route().handler(BodyHandler.create())
    router.get("/api/v1/user/").handler(this::helloVertx)
    router.get("/api/v1/user/:name").handler(this::helloWithHame)
    router.post("/api/v1/user/add").handler(this::createUser)

    doConfig(startPromise, router)
  }

  private fun doConfig(startPromise: Promise<Void>, router: Router) {
    val defaultConfig: ConfigStoreOptions = ConfigStoreOptions()
      .setType("file")
      .setFormat("json")
      .setConfig(JsonObject().put("path", "config.json"))

    val opts = ConfigRetrieverOptions()
      .addStore(defaultConfig)
    val cfgRetriever = ConfigRetriever.create(vertx, opts)
    cfgRetriever.getConfig(startHttpServer(startPromise, router))
  }

  private fun startHttpServer(
    startPromise: Promise<Void>,
    router: Router
  ): Handler<AsyncResult<JsonObject>> {
    return Handler {
      if (it.succeeded()) {
        val config = it.result()
        val http = config.getJsonObject("http")
        val httpPort = http.getInteger("port")
        vertx.createHttpServer().requestHandler(router).listen(httpPort)
        startPromise.complete()
      } else {
        //Figure out there
        startPromise.fail("Unable to load configuration")
      }
    }
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
