import com.tino1b2be.kotlob.Order
import com.tino1b2be.kotlob.OrderBook
import com.tino1b2be.kotlob.OrderType
import com.tino1b2be.kotlob.Util
import java.util.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun generateRandomOrders(num: Int): LinkedList<Order> {

    val ls = LinkedList<Order>()

    // generate bids with price between 100 and 120
    for (i in 0..num) {
        val price: Int = (100..120).random()
        val type = OrderType.BID
        val quantity: Int = (1..100).random()
        ls.add(Order(price, quantity, type))
    }

    // generate asks with price between 121 and 140
    for (i in 0..num) {
        val price: Int = (121..140).random()
        val type = OrderType.ASK
        val quantity: Int = (1..100).random()
        ls.add(Order(price, quantity, type))
    }

    // generate random orders with price between 100 and 140
    for (i in 0..(num * 2)) {
        val price: Int = (100..140).random()
        val type = OrderType.values().toList().shuffled().first()
        val quantity: Int = (1..100).random()
        ls.add(Order(price, quantity, type))
    }

    return ls
}

fun main() {

    val orderBook = OrderBook()
    var orders: LinkedList<Order>

    val executionTimeforGenOrders = measureTimeMillis {
        orders = generateRandomOrders(1000000)
    }

    println("total time to generate orders = ${executionTimeforGenOrders}ms")

    val executionTime = measureTimeMillis {
        orders.forEach { orderBook.processLimitOrder(it) }
    }

    println("total time to process ${orders.size} orders = ${executionTime}ms")
    println("total number of trades = ${orderBook.trades.size}")
//    println(orderBook.getRecentTrades())

}

fun main2() {

    val orderBook = OrderBook()
    val sampleOrders = Util.loadOrdersFromJsonFile("src/sample_orders.json")

    // add sample orders
    for (order in sampleOrders) {
        orderBook.processLimitOrder(order)
    }
    println("Welcome to the KotLOB limit order book interactive CLI")

    while (true) {

        println("Please select your desired operation:\n")
        println("1. Print Order Book JSON")
        println("2. Add new limit order")
        println("3. Recent trades JSON")
        println("4. Quit")
        print("What is your option ( type 1, 2, 3 or 4 ) ? : ")

        when (Integer.valueOf(readLine())) {
            1 -> { // print order book
                println(orderBook.getOrderBookJSON())
            }
            2 -> { // Add new limit order
                print("Order type (enter \"bid\" or \"ask\") : ")
                val typeString = readLine()
                val type: OrderType = if (typeString == "bid") {
                    OrderType.BID
                } else if (typeString == "ask") {
                    OrderType.ASK
                } else {
                    println("Invalid input. Try again.")
                    continue
                }
                print("")
                println("")

                print("Quantity : ")
                val qty = Integer.valueOf(readLine())
                print("")
                println("")

                print("Price : ")
                val price = Integer.valueOf(readLine())
                print("")
                println("")

                val order = Order(price = price, quantity = qty, type = type)

                orderBook.processLimitOrder(order)

                when {
                    order.isFulfilled() -> {
                        println("Your order with ID ${order.id} fulfilled by the following trades:")
                        for (trade in order.orderTrades) {
                            println(trade)
                        }
                    }
                    order.orderTrades.size > 0 -> {
                        println("Your order with ID ${order.id} has been partially fulfilled by the following trades:")
                        for (trade in order.orderTrades) {
                            println(trade)
                        }
                    }
                    else -> {
                        println("Your order with ID ${order.id} has been added to the order book without any trades.")
                    }
                }
            }
            3 -> {
                println(orderBook.getRecentTrades())
            }
            4 -> {
                exitProcess(0)
            }
            else -> {
                println("Invalid input. try again")
                continue
            }

        }

    }
}