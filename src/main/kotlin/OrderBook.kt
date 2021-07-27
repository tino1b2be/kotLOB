import java.util.*

class OrderBook {

    var asks = PriorityQueue<OrdersAtPrice>()   // priority queue with lowest asking price at top
    var bids = PriorityQueue<OrdersAtPrice>(Collections.reverseOrder())  // priority queue with highest bidding price at top

    fun processOrder(){
        TODO("add code to process the order")
    }

    fun isEmpty(): Boolean { return asks.isEmpty() && bids.isEmpty()}

}