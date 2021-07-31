import java.util.*

/**
 * Represents an order in the order book
 */
class Order(
    val price: Int,
    var quantity: Int,
    val type: OrderType,
    val timestamp: Date = Calendar.getInstance().time,
    val id: UUID = UUID.randomUUID(),
    // arraylist to store all trades that happen in the order book
    private var orderTrades: ArrayList<Trade> = ArrayList<Trade>()
) {

    /**
     * returns true if this order has been fulfilled
     */
    fun isFulfilled(): Boolean {
        return quantity == 0 && orderTrades.isNotEmpty()
    }

    /**
     * returns true if this order has been fulfilled
     */
    fun isNotFulfilled(): Boolean {
        return !isFulfilled()
    }


    /**
     * add a [newTrade] for this order and deduct the order quantity
     */
    fun addTrade(newTrade: Trade) {
        orderTrades.add(newTrade)
        quantity -= newTrade.quantity
    }

    /**
     * Order fulfilled by the [newTrade].
     * All quantity from this order is bought/sold.
     */
    fun fulfill(newTrade: Trade) {
        quantity = 0
        addTrade(newTrade)
    }

    override fun toString(): String {
        return "$type | qty=$quantity   | price=$price"
    }

}

