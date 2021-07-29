import java.util.*

class ListOrdersAtPrice(
    val price: Int
) : Comparable<ListOrdersAtPrice> {

    var orders: LinkedList<Order> = LinkedList<Order>()
    var quantityTotal: Int = 0


    fun addOrder(newOrder: Order) {
        this.quantityTotal += newOrder.quantity
        this.orders.add(newOrder)

    }

    override fun compareTo(other: ListOrdersAtPrice): Int {
        return this.orders.peek().price - other.orders.peek().price
    }

    operator fun get(i: Int): Order {
        return orders[i]
    }

    fun removeOrderAt(i: Int) {
        orders.removeAt(i)
    }

    fun processTradesBidOrder(newBidOrder: Order, orderBook: OrderBook) {

        while ((orders.size > 0) && (newBidOrder.quantity > 0)) {
            var newTrade: Trade;
            val currentAskOrder = orders.peek()
            if (newBidOrder.quantity < currentAskOrder.quantity) {
                /// quantity from seller is more than the buyer wants to buy
                currentAskOrder.quantity -= newBidOrder.quantity
                newTrade = Trade(
                    buyer = newBidOrder,
                    seller = currentAskOrder,
                    quantity = newBidOrder.quantity,
                    OrderType.BID,
                    sequence = orderBook.sequence++
                )
                newBidOrder.fullfill(newTrade)
                currentAskOrder.addTrade(newTrade)
            } else {
                // the quantity from seller is less than what the buyer wants to buy
                newBidOrder.quantity -= currentAskOrder.quantity
                newTrade = Trade(
                    buyer = newBidOrder,
                    seller = currentAskOrder,
                    quantity = currentAskOrder.quantity,
                    OrderType.BID,
                    sequence = orderBook.sequence++
                )
                currentAskOrder.fullfill(newTrade)
                newBidOrder.addTrade(newTrade)
                // remove fullfillsed order from orderbook
                orders.pop()
            }
            orderBook.addTrade(newTrade)
        }

    }

    fun processTradesAskOrder(newAskOrder: Order, orderBook: OrderBook) {

        while ((orders.size > 0) && (newAskOrder.quantity > 0)) {
            var newTrade: Trade;
            val currentBidOrder = orders.peek()

            if (newAskOrder.quantity < currentBidOrder.quantity) {
                /// quantity from seller is LESS than the buyer wants to buy
                newTrade = Trade(
                    buyer = newAskOrder,
                    seller = currentBidOrder,
                    quantity = newAskOrder.quantity,
                    OrderType.ASK,
                    sequence = orderBook.sequence++
                )
                newAskOrder.fullfill(newTrade)
                currentBidOrder.addTrade(newTrade)
            } else {
                // the quantity from seller is MORE than what the buyer wants to buy
                newTrade = Trade(
                    buyer = newAskOrder,
                    seller = currentBidOrder,
                    quantity = currentBidOrder.quantity,
                    OrderType.ASK,
                    sequence = orderBook.sequence++
                )
                currentBidOrder.fullfill(newTrade)
                newAskOrder.addTrade(newTrade)
                // remove fullfillsed order from orderbook
                orders.pop()
            }
            // add trade to orderbook
            orderBook.addTrade(newTrade)
        }

    }

    fun getSize(): Int {
        return orders.size
    }
}
