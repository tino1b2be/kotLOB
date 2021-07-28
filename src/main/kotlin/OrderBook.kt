import OrderType.*
import java.util.*

class OrderBook {

    // priority queue with lowest asking price at top
    var asks = PriorityQueue<OrdersAtPrice>()
    // priority queue with highest bidding price at top
    var bids = PriorityQueue<OrdersAtPrice>(Collections.reverseOrder())
    // arraylist to store all trades that happen in the orderbook
    var trades: ArrayList<Trade> = ArrayList<Trade>()

    fun processLimitOrder(newOrder: Order) {

        when (newOrder.type) {
            ASK -> {
                processAskOrder(newOrder)
            }
            BID -> {
                processBidOrder(newOrder)
            }
        }

    }

    /**
     * Processes the [newBidOrder] as a Bid Limit Order.
     */
    private fun processBidOrder(newBidOrder: Order) {
        // Process trades at each OrdersAtPrice list inside asks starting with the lowest asking price until the asking price is higher than the bidding price

        while ((!asks.isEmpty()) && newBidOrder.quantity > 0 && newBidOrder.price >= asks.peek().price) {
            val lowestAsks: OrdersAtPrice = asks.peek()
             lowestAsks.processTradesBidOrder(newBidOrder)
            // remove list if there are no more orders at this price.
            if (lowestAsks.getSize() == 0) asks.poll()
        }

        // All the best prices are finished. If there's still quantity remaining, add order to book
        if (newBidOrder.quantity > 0) {
            insertOrderIntoBids(newBidOrder)
        }

    }

    /**
     * Processes the [newAskOrder] as an Ask Limit Order
     */
    private fun processAskOrder(newAskOrder: Order) {
        // Process trades at each OrdersAtPrice list inside bids starting with the highest bidding price until the bidding price is lower than asking price

        while ( (!bids.isEmpty()) && (newAskOrder.quantity > 0) && (newAskOrder.price <= bids.peek().price) ) {
            val highestBids: OrdersAtPrice = asks.peek()
            highestBids.processTradesAskOrder(newAskOrder)
            // remove list if there are no more orders at this price.
            if (highestBids.getSize() == 0) asks.poll()
        }

        // All the best prices are finished. If there's still quantity remaining, add order to book
        if (newAskOrder.quantity > 0) {
            insertOrderIntoAsks(newAskOrder)
        }
    }

    private fun insertOrderIntoAsks(newAskOrder: Order) {
        // TODO("Find the patching price in the Asks nd add the new order to the list")
    }

    private fun insertOrderIntoBids(newOrder: Order) {
        // TODO("Find the patching price in the Bids and add the new order to the list")
    }

    fun isEmpty(): Boolean {
        return asks.isEmpty() && bids.isEmpty()
    }

}