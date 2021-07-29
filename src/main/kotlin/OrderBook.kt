import OrderType.*
import java.util.*
import kotlin.collections.HashMap

class OrderBook(
    // priority queue with lowest asking price at top
    var asks: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(),
    // priority queue with highest bidding price at top
    var bids: PriorityQueue<ListOrdersAtPrice> = PriorityQueue<ListOrdersAtPrice>(Collections.reverseOrder()),
    // arraylist to store all trades that happen in the orderbook
    var trades: ArrayList<Trade> = ArrayList<Trade>(),
    // a Map to quickly search for the listOrdersAtPrice
    var listPriceMap: HashMap<Int, ListOrdersAtPrice> = HashMap< Int, ListOrdersAtPrice >()
) {


    fun addTrade( trade: Trade){
        trades.add(trade)
    }

    fun processLimitOrder(newOrder: Order) {

        if (newOrder.type == ASK) {
            processAskOrder(newOrder)
        }
        else {
            processBidOrder(newOrder)
        }

        // All the best prices are finished. If there's still quantity remaining, add order to book
        if (newOrder.quantity > 0) {
            insertIntoOrderBook(newOrder)
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

        while ( (!bids.isEmpty()) && (newAskOrder.quantity > 0) && (newAskOrder.price <= bids.peek().price) ) {
            val highestBids: ListOrdersAtPrice = asks.peek()
            highestBids.processTradesAskOrder(newAskOrder, this)
            // remove list if there are no more orders at this price.
            if (highestBids.getSize() == 0) asks.poll()
        }
    }

    private fun insertIntoOrderBook(newBidOrder: Order) {
        // search for the list of orders at the price of the new order = listAtPrice
        // add this newBidOrder to the listAtPrice list
        var listAtPrice: ListOrdersAtPrice = getListAtPrice(newBidOrder)
        listAtPrice.addOrder(newBidOrder)
    }

    /**
     * Search the map of prices and return the list of orders at the price of [order]
     */
    private fun getListAtPrice(order: Order): ListOrdersAtPrice {
        // Search for a list with the price of the new order
        // if you can't find the list, create and return a new empty one
        var listPrice = listPriceMap[order.price]
        if (listPrice != null){
            return listPrice
        } else {
            listPrice = ListOrdersAtPrice(order.price)
            // add list to orderbook priority queue and Map
            listPriceMap.put(order.price, listPrice)
            if (order.type == BID) bids.add(listPrice) else asks.add(listPrice)

            return listPrice
        }
    }

    fun isEmpty(): Boolean {
        return asks.isEmpty() && bids.isEmpty()
    }

}