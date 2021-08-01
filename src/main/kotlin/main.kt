import com.tino1b2be.kotlob.Order
import com.tino1b2be.kotlob.OrderBook
import com.tino1b2be.kotlob.OrderType
import com.tino1b2be.kotlob.Util
import kotlin.system.exitProcess

fun main() {

    val orderBook = OrderBook()
    val sampleOrders = Util.loadOrdersFromJsonFile("src/sample_orders.json")

    // add sample orders
    for ( order in sampleOrders){
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