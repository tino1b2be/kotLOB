import java.util.*

class OrdersAtPrice : Comparable<OrdersAtPrice> {

    var orders: Queue<Order> = LinkedList<Order>()
    var price: Int = 0



    override fun compareTo(other: OrdersAtPrice): Int {
        return this.orders.peek().price - other.orders.peek().price
    }
}
