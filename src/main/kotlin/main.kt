import javax.management.InvalidAttributeValueException

fun main() {

    var orderbook = OrderBook();
    var sampleOrders = Util.loadOrdersFromJsonFile("src/sample_orders.json")

    // add sample orders
    for ( order in sampleOrders){
        orderbook.processLimitOrder(order)
    }
    println("Welcome to the KotLOB limit order book interactive CLI")

    while (true) {

        println("Please select your desired operation:\n")
        println("1. Print Order Book JSON")
        println("2. Add new limit order")
        println("3. Recent trades JSON")
        print("What is your option ( type 1, 2 or 3 ) ? : ")
        val option = Integer.valueOf(readLine())

        when (option) {
            1 -> { // print order book
                println(orderbook.getOrderBookJSON())
            }
            2 -> { // Add new limit order
                print("Order type (enter \"bid\" or \"ask\") : ")
                val typeString = readLine()
                val type: OrderType
                if (typeString == "bid") {
                    type = OrderType.BID
                } else if (typeString == "ask") {
                    type = OrderType.ASK
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

                orderbook.processLimitOrder(order)

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
                println(orderbook.getRecentTrades())
            }
            else -> {
                println("Invalid input. try again")
                continue
            }

        }

    }
}