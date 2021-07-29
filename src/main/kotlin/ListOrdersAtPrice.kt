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
            val currentOrder = orders.peek()
            if (newBidOrder.quantity < currentOrder.quantity) {
                /// quantity from seller is more than the buyer wants to buy
                currentOrder.quantity -= newBidOrder.quantity
                newTrade = Trade(buyer = newBidOrder, seller = currentOrder, quantity = newBidOrder.quantity)
                newBidOrder.fullfill(newTrade)
                currentOrder.addTrade(newTrade)
            } else {
                // the quantity from seller is less than what the buyer wants to buy
                newBidOrder.quantity -= currentOrder.quantity
                newTrade = Trade(buyer = newBidOrder, seller = currentOrder, quantity = currentOrder.quantity)
                currentOrder.fullfill(newTrade)
                newBidOrder.addTrade(newTrade)
                // remove fullfillsed order from orderbook
                orders.pop()
            }
            orderBook.addTrade(newTrade)
        }

    }

    fun processTradesAskOrder(newAskOrder: Order, orderBook: OrderBook) {
        TODO("Process trades for the new bid order with orders in this list at this price")
    }

    fun getSize(): Int {
        return orders.size
    }
}
