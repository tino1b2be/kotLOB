import com.tino1b2be.kotlob.Order
import com.tino1b2be.kotlob.OrderBook
import com.tino1b2be.kotlob.OrderType
import com.tino1b2be.kotlob.Util
import io.vertx.core.AbstractVerticle
import io.vertx.core.http.HttpServerRequest
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler


const val API_TRADE_HISTORY = "/v1/orderbook/tradehistory"
const val API_SUBMIT_LIMIT_ORDER = "/v1/orderbook/limit"
const val API_GET_ORDER_BOOK = "/v1/book/orderbook/orders"

class KotlobAPI(
    var orderBook: OrderBook = OrderBook()
) : AbstractVerticle() {

    init {
        val sampleOrders = Util.loadOrdersFromJsonFile("src/sample_orders.json")
        // add sample orders
        for ( order in sampleOrders){
            orderBook.processLimitOrder(order)
        }

    }
    override fun start() {

        val router = Router.router(vertx)

        router.get(API_TRADE_HISTORY).handler(this::getTradeHistory)
        router.get(API_GET_ORDER_BOOK).handler(this::getOrderBook)
        router.post(API_SUBMIT_LIMIT_ORDER).handler(BodyHandler.create()).handler(this::submitLimitOrder)

        vertx.createHttpServer().requestHandler(router).listen(8080)

        println("----------------------------------------------")
        println("-----> Valhala now listening on localhost:8080")
        println("----------------------------------------------")
    }

    private fun getOrderBook(routingContext: RoutingContext) {

        val response = routingContext.response()
        val returnJson = orderBook.getOrderBookJSON()

        response.putHeader("content-type", "application/json")
            .setChunked(true)
            .write(returnJson)
        response.end()
    }

    private fun getTradeHistory(routingContext: RoutingContext) {

        val response = routingContext.response()
        val returnJson = orderBook.getRecentTrades()

        response.putHeader("content-type", "application/json")
            .setChunked(true)
            .write(returnJson)

        response.end()
    }

    /**
     * submit a limit order into the orderbook
     */
    private fun submitLimitOrder(routingContext: RoutingContext) {

        val request = routingContext.request()

        if (isValid(routingContext.request())) {
            val postedPrice = Integer.valueOf(request.getParam(("price")))
            val postedQty = Integer.valueOf(request.getParam(("qty")))
            val postedType = if (request.getParam("type").lowercase() == "bid") OrderType.BID else OrderType.ASK

            val order = Order(price = postedPrice, quantity = postedQty, type = postedType)
            orderBook.processLimitOrder(order)
            println("Order created: $order")
            val returnString = order.getJSONstatus()
            val response = routingContext.response()
            response.putHeader("Content-Type", "application/json")
                .putHeader("Content-Length", returnString.length.toString())
                .setStatusCode(201)
                .write(returnString)
            response.end()
        } else {
            val response = routingContext.response()
            response.putHeader("content-type", "application/json")
                .setStatusCode(422)
                .write("{\"message\":\"Invalid input\"}")
            response.end()
        }
    }

    /**
     * return true if the contents of [request] create a valid order
     */
    private fun isValid(request: HttpServerRequest): Boolean {

        try {
            Integer.valueOf(request.getParam(("price")))
            Integer.valueOf(request.getParam("qty"))
            val typ = request.getParam("type")
            if (typ.lowercase() != "bid" && typ.lowercase() != "ask") return false
            return true
        } catch (e: Exception) {
            return false
        }
    }

    override fun stop() {
        println("Bye")
    }

//    companion object {
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val vertx: Vertx = Vertx.vertx()
//            vertx.deployVerticle(KotlobAPI())
//        }
//    }
}