import io.vertx.core.Vertx

fun main() {
    val vertx: Vertx = Vertx.vertx()
    vertx.deployVerticle(KotlobAPI())
}