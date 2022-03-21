import com.example.technology.HelloWorker
import com.example.technology.MainVerticle
import io.vertx.config.ConfigRetriever
import io.vertx.config.ConfigRetrieverOptions
import io.vertx.config.ConfigStoreOptions
import io.vertx.core.Vertx
import io.vertx.core.http.HttpServer
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router

fun main(args: Array<String>) {
  val vertx = Vertx.vertx()

  val server: HttpServer = vertx.createHttpServer()
  val router = Router.router(vertx)

  val mainVerticle = MainVerticle(router)
  val helloVerticle = HelloWorker()

  vertx.deployVerticle(mainVerticle)
  vertx.deployVerticle(helloVerticle, io.vertx.core.DeploymentOptions().setWorker(true))

  //set config store
  val defaultConfig: ConfigStoreOptions = ConfigStoreOptions()
    .setType("file")
    .setFormat("json")
    .setConfig(JsonObject().put("path", "config.json"))

  val opts = ConfigRetrieverOptions()
    .addStore(defaultConfig)
  val cfgRetriever = ConfigRetriever.create(vertx, opts)
  cfgRetriever.getConfig() {
    if (it.succeeded()) {
      val config = it.result()
      val http = config.getJsonObject("http")
      val httpPort = http.getInteger("port")
      server.requestHandler(router).listen(httpPort)
    } else {
      println("Fail to start server")
    }
  }
}


