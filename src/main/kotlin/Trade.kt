import java.util.*

/**
 * Represents a trade in the order book
 */
class Trade(
    var buyer: Order,
    var seller: Order,
    var quantity: Int,
    val taker: OrderType,
    val sequence: Int,
    var id: UUID = UUID.randomUUID(),
    var timestamp: Date = Calendar.getInstance().time
) {

    /**
     * returns a JSON formatted string representation of the trade
     */
    override fun toString(): String {
        var takerString: String = if (taker == OrderType.ASK) "ask" else "buy"
        return "{\n" +
                "'price':'${buyer.price}',\n" +
                "'quantity':'$quantity',\n" +
                "'tradedAt':'$timestamp',\n" +
                "'takerSide':'$takerString',\n" +
                "'sequenceId':$sequence,\n" +
                "'id':'$id',\n" +
                "}"
    }
}
