import com.example.technology.MainVerticle
import io.vertx.core.Vertx

fun main() {
  val vertx = Vertx.vertx()
  vertx.deployVerticle(MainVerticle())
}

