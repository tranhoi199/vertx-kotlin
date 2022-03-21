package com.example.technology

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise

class HelloVerticle : AbstractVerticle() {
  override fun start(startPromise: Promise<Void>?) {
    vertx.eventBus().consumer<String>("user.vertx.addr") {
      it.reply("Hello Come To Vert.x World!");
    }

    vertx.eventBus().consumer<String>("user.name.addr") {
      val name = it.body().toString()
      it.reply(String.format("Hello %s! From EventBus", name))
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
