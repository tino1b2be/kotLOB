package com.tino1b2be.kotlob

import com.tino1b2be.kotlob.OrderType.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Implementation of a Limit Order Book
 */
class OrderBook(

    var asks: TreeMap<Int, ListOrdersAtPrice> = TreeMap(),
    var bids: TreeMap<Int, ListOrdersAtPrice> = TreeMap(Collections.reverseOrder()),

//    // priority queue with lowest asking price at top
//    var asks: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(),
//    // priority queue with highest bidding price at top
//    var bids: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(Collections.reverseOrder()),

    // arraylist to store all trades that happen in the order book TODO("could possibly optimise trades list")
    var trades: ArrayList<Trade> = ArrayList<Trade>(),
    // a Map to quickly search for the listOrdersAtPrice
    var listPriceMap: HashMap<Int, ListOrdersAtPrice> = HashMap(),
    var lastUpdateTime: Date = Calendar.getInstance().time,
    var sequence: Int = 0,
    var numOrders: Int = 0
) {

    /**
     * Add trade record to order book
     */
    fun addTrade(trade: Trade) {
        trades.add(trade)
    }

    fun getRecentTrades(): String {
        return getRecentTrades(20)
    }

    fun getRecentTrades(num: Int): String {

        var returnString = "[\n"

        for ((index, trade) in trades.withIndex()) {
            var tradeString = " {\n" +
                    "  \"price\":\"${trade.price}\",\n" +
                    "  \"quantity\":\"${trade.quantity}\",\n" +
                    "  \"tradedAt\":\"${trade.timestamp}\",\n" +
                    "  \"takerSide\":\"${trade.taker}\",\n" +
                    "  \"sequence\":\"${trade.sequence}\",\n" +
                    "  \"id\":\"${trade.id}\"\n" +
                    " }"
            if (index < num && index < trades.size - 1) tradeString = "$tradeString,\n" // add comma
            returnString = "$returnString $tradeString"
            if (index >= num) break
        }

        returnString = "$returnString\n]"
        return returnString

    }

    /**
     * Return JSON formatted string of the top [num] bids and [num] asks from the order book
     */
    fun getOrderBookJSON(num: Int): String {

        var returnString = "{\n \"Asks\":[\n"
        // get top [num] asks
        var index = 0
        for (listOfOrders in asks) {
            var listString = " {\n" +
                    "   \"side\":\"sell\",\n" +
                    "   \"quantity\":${listOfOrders.value.quantityTotal},\n" +
                    "   \"price\":${listOfOrders.value.price},\n" +
                    "   \"orderCount\":${listOfOrders.value.getSize()}\n" +
                    "  }"

            if (index < num && index < asks.size - 1) listString = "$listString,\n" // add comma
            returnString = "$returnString $listString"
            index++
            if (index >= num) break
        }

        returnString = "$returnString\n ],\n \"Bids\":[\n"

        // get top [num] bids
        index = 0
        for (listOfOrders in bids) {
            var listString = " {\n" +
                    "   \"side\":\"buy\",\n" +
                    "   \"quantity\":${listOfOrders.value.quantityTotal},\n" +
                    "   \"price\":${listOfOrders.value.price},\n" +
                    "   \"orderCount\":${listOfOrders.value.getSize()}\n" +
                    "   }"

            if (index < num && index < bids.size - 1) listString = "$listString,\n" // add comma
            returnString = "$returnString $listString" // append orders to string
            index++
            if (index >= num) break
        }

        // TODO format the date to ISO 8601
        returnString = "$returnString \n  ],\n  \"LastChange\": \"$lastUpdateTime\"\n}"
        return returnString
    }

    /**
     * Return JSON formatted string of the top 20 bids and 20 asks from the order book
     */
    fun getOrderBookJSON(): String {
        return getOrderBookJSON(20)
    }

    /**
     * Process a [newLimitOrder] and add it to the order book
     */
    fun processLimitOrder(newLimitOrder: Order) {

        if (newLimitOrder.type == ASK) {
            processAskOrder(newLimitOrder)
        } else {
            processBidOrder(newLimitOrder)
        }

        // All the best prices are finished. If there's still quantity remaining, add order to book
        if (newLimitOrder.quantity > 0) {
            insertIntoOrderBook(newLimitOrder)
        }
        numOrders++
        lastUpdateTime = Calendar.getInstance().time
    }

    /**
     * Processes the [newBidOrder] as a Bid Limit Order.
     */
    private fun processBidOrder(newBidOrder: Order) {
        // Process trades at each OrdersAtPrice list inside asks starting with the lowest asking price until the asking price is higher than the bidding price

        while ((!asks.isEmpty()) && newBidOrder.quantity > 0 && newBidOrder.price >= asks.firstKey()) {
            val lowestAsks: ListOrdersAtPrice? = asks[asks.firstKey()]
            lowestAsks?.processTradesBidOrder(newBidOrder, this)
            // remove list if there are no more orders at this price.
            if (lowestAsks != null) {
                if (lowestAsks.getSize() == 0) {
                    asks.remove(lowestAsks.price)
                    listPriceMap.remove(lowestAsks.price)
                }
            }
        }

    }

    /**
     * Processes the [newAskOrder] as an Ask Limit Order
     */
    private fun processAskOrder(newAskOrder: Order) {
        // Process trades at each OrdersAtPrice list inside bids starting with the highest bidding price until the bidding price is lower than asking price

        while ((!bids.isEmpty()) && (newAskOrder.quantity > 0) && (newAskOrder.price <= bids.firstKey())) {
            val highestBids: ListOrdersAtPrice? = bids[bids.firstKey()]
            highestBids?.processTradesAskOrder(newAskOrder, this)
            // remove list if there are no more orders at this price.
            if (highestBids != null) {
                if (highestBids.getSize() == 0) {
                    bids.remove(highestBids.price)
                    listPriceMap.remove(highestBids.price)
                }
            }
        }
    }

    /**
     * Add [newOrder] to the order book.
     */
    private fun insertIntoOrderBook(newOrder: Order) {
        // search for the list of orders at the price of the new order = listAtPrice
        // add this newBidOrder to the listAtPrice list
        val listAtPrice: ListOrdersAtPrice = getListAtPrice(newOrder)
        listAtPrice.addOrder(newOrder)
    }

    /**
     * Search the map of prices and return the list of orders at the price of [order]
     */
    private fun getListAtPrice(order: Order): ListOrdersAtPrice {
        // Search for a list with the price of the new order
        // if you can't find the list, create and return a new empty one
        var listPrice = listPriceMap[order.price]
        return if (listPrice != null) {
            listPrice
        } else {
            listPrice = ListOrdersAtPrice(order.price)
            // add list to order book priority queue and Map
            listPriceMap[order.price] = listPrice
            if (order.type == BID) bids[order.price] = listPrice else asks[order.price] = listPrice
            listPrice
        }
    }

    /**
     * returns true is the order book has no orders
     */
    fun isEmpty(): Boolean {
        return asks.isEmpty() && bids.isEmpty()
    }

}