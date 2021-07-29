import java.util.*

class Order(
    val price: Int,
    var quantity: Int,
    val type: OrderType,
    val timestamp: Date = Calendar.getInstance().time,
    val orderId: UUID = UUID.randomUUID(),
    // arraylist to store all trades that happen in the orderbook
    var orderTrades: ArrayList<Trade> = ArrayList<Trade>()
) {

    fun isFullfilled(): Boolean {
        return quantity == 0
    }

    /**
     * add a [newTrade] for this order and deduct the order quantity
     */
    fun addTrade(newTrade: Trade) {
        orderTrades.add(newTrade)
        quantity -= newTrade.quantity
    }

    /**
     * Order fullfilled by the [newTrade].
     * All quantity from this order is bought/sold.
     */
    fun fullfill(newTrade: Trade) {
        quantity = 0
        addTrade(newTrade)
    }

}

