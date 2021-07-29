import java.util.*

/**
 * Stores a list or orders for a given price and order type/side
 */
class ListOrdersAtPrice(
    val price: Int
) : Comparable<ListOrdersAtPrice> {

    var orders: LinkedList<Order> = LinkedList<Order>()
    var quantityTotal: Int = 0

    /**
     * Add a new order to this list
     */
    fun addOrder(newOrder: Order) {
        this.quantityTotal += newOrder.quantity
        this.orders.add(newOrder)

    }

    override fun compareTo(other: ListOrdersAtPrice): Int {
//        This compareTo class is used for the PriorityQueue implementation
//        to allow us to compare the prices or orders in this class with other
//        price tiers.
        return this.orders.peek().price - other.orders.peek().price
    }

    operator fun get(i: Int): Order {
        return orders[i]
    }

    fun removeOrderAt(i: Int) {
        orders.removeAt(i)
    }

    /**
     * Process trades for the [newBidOrder] with the sell orders in this list
     */
    fun processTradesBidOrder(newBidOrder: Order, orderBook: OrderBook) {

        while ((orders.size > 0) && (newBidOrder.quantity > 0)) {
            var newTrade: Trade
            val currentAskOrder = orders.peek()
            if (newBidOrder.quantity < currentAskOrder.quantity) {
                /// quantity from seller is more than the buyer wants to buy
                quantityTotal -= newBidOrder.quantity
                newTrade = Trade(
                    buyer = newBidOrder,
                    seller = currentAskOrder,
                    quantity = newBidOrder.quantity,
                    OrderType.BID,
                    sequence = orderBook.sequence++
                )
                newBidOrder.fulfill(newTrade)
                currentAskOrder.addTrade(newTrade)
            } else {
                // the quantity from seller is less than what the buyer wants to buy
                quantityTotal -= currentAskOrder.quantity
                newTrade = Trade(
                    buyer = newBidOrder,
                    seller = currentAskOrder,
                    quantity = currentAskOrder.quantity,
                    OrderType.BID,
                    sequence = orderBook.sequence++
                )
                currentAskOrder.fulfill(newTrade)
                newBidOrder.addTrade(newTrade)
                // remove fulfilled order from order book
                orders.pop()
            }
            orderBook.addTrade(newTrade)
        }

    }

    /**
     * Process trades for the [newAskOrder] with the bid orders in this list
     */
    fun processTradesAskOrder(newAskOrder: Order, orderBook: OrderBook) {

        // Go through each sell order in this list until all the quantity for [newAskOrder] has been fulfilled.
        while ((orders.size > 0) && (newAskOrder.quantity > 0)) {
            var newTrade: Trade
            val currentBidOrder = orders.peek()

            if (newAskOrder.quantity < currentBidOrder.quantity) {
                /// quantity from seller is LESS than the buyer wants to buy
                quantityTotal -= newAskOrder.quantity
                newTrade = Trade(
                    buyer = newAskOrder,
                    seller = currentBidOrder,
                    quantity = newAskOrder.quantity,
                    OrderType.ASK,
                    sequence = orderBook.sequence++
                )
                newAskOrder.fulfill(newTrade)
                currentBidOrder.addTrade(newTrade)
            } else {
                // the quantity from seller is MORE than what the buyer wants to buy
                quantityTotal -= currentBidOrder.quantity
                newTrade = Trade(
                    buyer = newAskOrder,
                    seller = currentBidOrder,
                    quantity = currentBidOrder.quantity,
                    OrderType.ASK,
                    sequence = orderBook.sequence++
                )
                currentBidOrder.fulfill(newTrade)
                newAskOrder.addTrade(newTrade)
                // remove fulfilled order from order book
                orders.pop()
            }
            // add trade to order book
            orderBook.addTrade(newTrade)
        }

    }

    fun getSize(): Int {
        return orders.size
    }
}
