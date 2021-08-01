package com.tino1b2be.kotlob

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
    val price: Int,
    var id: UUID = UUID.randomUUID(),
    var timestamp: Date = Calendar.getInstance().time
) {

    /**
     * returns a JSON formatted string representation of the trade
     */
    fun toJSONString(): String {
        var takerString: String = if (taker == OrderType.ASK) "ask" else "buy"
        return "{\n" +
                "'price':'${price}',\n" +
                "'quantity':'$quantity',\n" +
                "'tradedAt':'$timestamp',\n" +
                "'takerSide':'$takerString',\n" +
                "'sequenceId':$sequence,\n" +
                "'id':'$id',\n" +
                "}"
    }

    /**
     * Return a summary of the trade
     */
    override fun toString(): String {
        return "$taker | price=${price}   | qty=$quantity | seq=$sequence"
    }
}
