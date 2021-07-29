import OrderType.*
import java.util.*
import kotlin.collections.HashMap

/**
 * Implementation of a Limit Order Book
 */
class OrderBook(
    // priority queue with lowest asking price at top TODO("could possibly optimise asks")
    var asks: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(),
    // priority queue with highest bidding price at top TODO("could possibly optimise bids")
    var bids: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(Collections.reverseOrder()),
    // arraylist to store all trades that happen in the order book TODO("could possibly optimise trades list")
    var trades: ArrayList<Trade> = ArrayList<Trade>(),
    // a Map to quickly search for the listOrdersAtPrice
    var listPriceMap: HashMap<Int, ListOrdersAtPrice> = HashMap(),
    var lastUpdateTime: Date = Calendar.getInstance().time,
    var sequence: Int = 0
) {

    /**
     * Add trade record to order book
     */
    fun addTrade(trade: Trade) {
        trades.add(trade)
    }

    /**
     * Get all trades
     */
    fun getAllOrders() {
        TODO("return a linkedList with n prices from order book")
    }

    /**
     *  get the most recent [num] trades
     */
    fun getOrders(num: Int): LinkedList<ListOrdersAtPrice> {
        TODO("return a linkedList with n prices from order book")
    }

    /**
     * Return JSON formatted string of the top [num] bids and [num] asks from the order book
     */
    fun getOrderBookJSON(num: Int): String {

        var count = 0
        var returnString = "{\n \"Asks\":[\n  "
        // get top [num] asks
        for ((index, listOfOrders) in asks.withIndex()) {
            var listString = "{\n" +
                    "   \"side\":\"sell\",\n" +
                    "   \"quantity\":\"${listOfOrders.quantityTotal}\",\n" +
                    "   \"price\":\"${listOfOrders.price}\",\n" +
                    "   \"orderCount\":${listOfOrders.getSize()}\n" +
                    "}"

            if (index < num && index < asks.size) listString = "$listString," // add comma
            returnString = "$returnString $listString"
            if (index >= num) break
        }

        returnString = "$returnString\n  ],\n  \"Bids\":[\n  "

        // get top [num] bids
        for ((index, listOfOrders) in bids.withIndex()) {
            var listString = "{\n" +
                    "   \"side\":\"buy\",\n" +
                    "   \"quantity\":\"${listOfOrders.quantityTotal}\",\n" +
                    "   \"price\":\"${listOfOrders.price}\",\n" +
                    "   \"orderCount\":${listOfOrders.getSize()}\n" +
                    "}"

            if (index < num && index < bids.size) listString = "$listString," // add comma
            returnString = "$returnString $listString" // append orders to string
            if (index >= num) break
        }
        // TODO format the date to ISO 8601
        returnString = "$returnString \n  ],  \"LastChange\": \"$lastUpdateTime\"\n}"

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

    }

    /**
     * Processes the [newBidOrder] as a Bid Limit Order.
     */
    private fun processBidOrder(newBidOrder: Order) {
        // Process trades at each OrdersAtPrice list inside asks starting with the lowest asking price until the asking price is higher than the bidding price

        while ((!asks.isEmpty()) && newBidOrder.quantity > 0 && newBidOrder.price >= asks.peek().price) {
            val lowestAsks: ListOrdersAtPrice = asks.peek()
            lowestAsks.processTradesBidOrder(newBidOrder, this)
            // remove list if there are no more orders at this price.
            if (lowestAsks.getSize() == 0) asks.poll()
        }

    }

    /**
     * Processes the [newAskOrder] as an Ask Limit Order
     */
    private fun processAskOrder(newAskOrder: Order) {
        // Process trades at each OrdersAtPrice list inside bids starting with the highest bidding price until the bidding price is lower than asking price

        while ((!bids.isEmpty()) && (newAskOrder.quantity > 0) && (newAskOrder.price <= bids.peek().price)) {
            val highestBids: ListOrdersAtPrice = asks.peek()
            highestBids.processTradesAskOrder(newAskOrder, this)
            // remove list if there are no more orders at this price.
            if (highestBids.getSize() == 0) asks.poll()
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
            if (order.type == BID) bids.add(listPrice) else asks.add(listPrice)
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