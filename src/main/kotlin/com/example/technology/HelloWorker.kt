package com.example.technology

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import java.util.*
import kotlin.collections.HashMap

class HelloWorker : AbstractVerticle() {
  var verticalId = UUID.randomUUID().toString()

  override fun start(startPromise: Promise<Void>?) {
    vertx.eventBus().consumer<String>("user.vertx.addr") {
      it.reply(String.format("1 - Hello Come To Vert.x World! Verticle Id: %s", verticalId));
    }

    vertx.eventBus().consumer<String>("user.vertx.addr") {
      it.reply(String.format("2 - Hello Come To Vert.x World! Verticle Id: %s", verticalId));
    }

    vertx.eventBus().consumer<String>("user.name.addr") {
      val name = it.body().toString()
      it.reply(String.format("Hello %s! From EventBus %s", name, verticalId))
    }

    vertx.eventBus().consumer<String>("user.create.addr") {
      val params = HashMap<String, String>()
      val message = it.body().toString()

      val splitParam = message.split("&")
      for (keyValue in splitParam) {
        val split : List<String> = keyValue.split("=")
        params[split[0]] = split[1]
      }

      val userName = params["username"]
      it.reply(String.format("Welcome %s! Your account created successfully", userName))
    }
  }
}
