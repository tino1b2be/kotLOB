import org.junit.Test

class OrderBookTest {

    var sampleOrders = Util.loadOrdersFromJsonFile( "src/sample_orders.json" )

    @Test
    fun testCreateOrder(){
        val bidOrder = Order( price = 111, quantity = 9, type = OrderType.BID)
        val askOrder = Order( price = 111, quantity = 9, type = OrderType.ASK)

        assert(!(askOrder.id.toString() === bidOrder.id.toString()))
        assert(bidOrder.price == 111)
        assert(bidOrder.quantity == 9)
        assert(bidOrder.type == OrderType.BID)
        assert(bidOrder.isNotFulfilled())
        assert(askOrder.price == 111)
        assert(askOrder.quantity == 9)
        assert(askOrder.type == OrderType.ASK)
        assert(askOrder.isNotFulfilled())

    }

    @Test
    fun testEmptyOrderBook() {
        val orderBook = OrderBook()
        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 0)
        assert(orderBook.numOrders == 0)

    }

    @Test
    fun testAddOneBidOrder() {
        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order( price = 111, quantity = 9, type = OrderType.BID))

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.size == 1)
        assert(orderBook.bids.peek().orders.size == 1)
        assert(orderBook.bids.peek().price == 111)
        assert(orderBook.bids.peek().quantityTotal == 9)
        assert(orderBook.bids.peek().orders.peek().isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.bids.peek())
        assert(orderBook.numOrders == 1)

    }

    @Test
    fun testAddMultipleBidOrdersSamePrice() {

        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order( price = 111, quantity = 9, type = OrderType.BID))
        orderBook.processLimitOrder(Order( price = 111, quantity = 3, type = OrderType.BID))
        orderBook.processLimitOrder(Order( price = 111, quantity = 5, type = OrderType.BID))

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.size == 1)
        assert(orderBook.bids.peek().price == 111)
        assert(orderBook.bids.peek().orders.size == 3)
        assert(orderBook.bids.peek().quantityTotal == 17)
        for ( order in orderBook.bids.peek().orders) assert(order.isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.bids.peek())
        assert(orderBook.numOrders == 3)

    }

    @Test
    fun testAddOneAskOrder() {
        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order( price = 111, quantity = 9, type = OrderType.ASK))

        assert(!orderBook.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.asks.peek().orders.size == 1)
        assert(orderBook.asks.peek().price == 111)
        assert(orderBook.asks.peek().quantityTotal == 9)
        assert(orderBook.asks.peek().orders.peek().isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.asks.peek())
        assert(orderBook.numOrders == 1)

    }

    @Test
    fun testAddMultipleAskOrdersSamePrice() {

        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order( price = 111, quantity = 9, type = OrderType.ASK))
        orderBook.processLimitOrder(Order( price = 111, quantity = 3, type = OrderType.ASK))
        orderBook.processLimitOrder(Order( price = 111, quantity = 5, type = OrderType.ASK))

        assert(!orderBook.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.asks.peek().price == 111)
        assert(orderBook.asks.peek().orders.size == 3)
        assert(orderBook.asks.peek().quantityTotal == 17)
        for ( order in orderBook.asks.peek().orders) assert(order.isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.asks.peek())
        assert(orderBook.numOrders == 3)

    }

    @Test
    fun testTradeEqualOrders() {

        val orderBook = OrderBook()
        val ask = Order( price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order( price = 111, quantity = 9, type = OrderType.BID)
        orderBook.processLimitOrder(ask)
        orderBook.processLimitOrder(bid)

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.isNotEmpty())
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.isFulfilled())
        assert(bid.isFulfilled())



    }

    @Test
    fun testLargeBidTrade() {

    }

    @Test
    fun testSmallBidAndAskTrades() {

    }

    @Test
    fun testLargeBidAndAskTrades() {


    }

    @Test
    fun testEmptyTradeHistory() {

    }

    @Test
    fun testSmallTradeHistory() {

    }

    @Test
    fun testMaxTradeHistory() {

    }

    @Test
    fun isEmpty() {

    }

}