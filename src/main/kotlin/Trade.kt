import java.util.*

class Trade(
    var buyer: Order,
    var seller: Order,
    var quantity: Int,
    val taker: OrderType,
    val sequence: Int,
    var id: UUID = UUID.randomUUID(),
    var timestamp: Date = Calendar.getInstance().time
) {


    override fun toString(): String {
        var takerString: String
        if (taker == OrderType.ASK) takerString = "ask" else takerString = "buy"
        return "{\n" +
                "'price':'${buyer.price}',\n" +
                "'quantity':'$quantity',\n" +
                "'tradedAt':'${timestamp.toString()}',\n" +
                "'takerSide':'$takerString',\n" +
                "'sequenceId':$sequence,\n" +
                "'id':'$id',\n" +
                "}"
    }
}
