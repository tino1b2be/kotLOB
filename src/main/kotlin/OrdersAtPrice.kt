import java.util.*

class OrdersAtPrice(val orderPrice: Int) : Comparable<OrdersAtPrice>, MutableIterator<Order>, Iterable<Order> {

    var orders: LinkedList<Order> = LinkedList<Order>()
    var price: Int = 0
    var quantityTotal: Int = 0

    private var it: Int = -1

    // Iterable function.
    override fun iterator(): Iterator<Order> {
        it = 0
        return this
    }

    // MutableIterator function
    override fun next(): Order {
        return orders[it++]
    }

    // MutableIterator function
    override fun hasNext(): Boolean {
        return it < orders.size
    }

    // MutableIterator function
    override fun remove() {
        quantityTotal--
        orders.removeAt(it--)
    }

    fun addOrder(od: Order) {
        this.quantityTotal += od.quantity
        this.orders.add(od)

    }

    override fun compareTo(other: OrdersAtPrice): Int {
        return this.orders.peek().price - other.orders.peek().price
    }

    operator fun get(i: Int): Order {
        return orders[i]
    }

    fun removeOrderAt(i: Int) {
        orders.removeAt(i)
    }

    fun processTradesBidOrder(newBidOrder: Order): Int {
        TODO("Process trades for the new bid order with orders in this list at this price")
    }

    fun processTradesAskOrder(newAskOrder: Order) {
        TODO("Process trades for the new bid order with orders in this list at this price")
    }

    fun getSize(): Int {
        return orders.size
    }
}
