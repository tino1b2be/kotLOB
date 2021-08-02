import com.tino1b2be.kotlob.Order
import com.tino1b2be.kotlob.OrderBook
import com.tino1b2be.kotlob.OrderType
import com.tino1b2be.kotlob.Util
import org.junit.Test

class OrderBookTest {

    @Test
    fun testCreateOrder() {
        val bidOrder = Order(price = 111, quantity = 9, type = OrderType.BID)
        val askOrder = Order(price = 111, quantity = 9, type = OrderType.ASK)

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
        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testAddOneBidOrder() {
        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order(price = 111, quantity = 9, type = OrderType.BID))
        
        assert(!orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.size == 1)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.orders.size == 1)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.price == 111)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.quantityTotal == 9)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.orders.peek().isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.bids[orderBook.bids.firstKey()]!!)
        assert(orderBook.numOrders == 1)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testAddMultipleBidOrdersSamePrice() {

        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order(price = 111, quantity = 9, type = OrderType.BID))
        orderBook.processLimitOrder(Order(price = 111, quantity = 3, type = OrderType.BID))
        orderBook.processLimitOrder(Order(price = 111, quantity = 5, type = OrderType.BID))

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.size == 1)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.price == 111)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.orders.size == 3)
        assert(orderBook.bids[orderBook.bids.firstKey()]!!.quantityTotal == 17)
        for (order in orderBook.bids[orderBook.bids.firstKey()]!!.orders) assert(order.isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.bids[orderBook.bids.firstKey()]!!)
        assert(orderBook.numOrders == 3)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testAddOneAskOrder() {
        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order(price = 111, quantity = 9, type = OrderType.ASK))

        assert(!orderBook.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.orders.size == 1)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.price == 111)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.quantityTotal == 9)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.orders.peek().isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.listPriceMap[111] == orderBook.asks[orderBook.asks.firstKey()]!!)
        assert(orderBook.numOrders == 1)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testAddMultipleAskOrdersSamePrice() {

        val orderBook = OrderBook()
        orderBook.processLimitOrder(Order(price = 111, quantity = 9, type = OrderType.ASK))
        orderBook.processLimitOrder(Order(price = 111, quantity = 3, type = OrderType.ASK))
        orderBook.processLimitOrder(Order(price = 111, quantity = 5, type = OrderType.ASK))

        assert(!orderBook.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.price == 111)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.orders.size == 3)
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.quantityTotal == 17)
        for (order in orderBook.asks[orderBook.asks.firstKey()]!!.orders) assert(order.isNotFulfilled())
        assert(orderBook.trades.isEmpty())
        assert(orderBook.listPriceMap.size == 1)
        assert(orderBook.listPriceMap[111] == orderBook.asks[orderBook.asks.firstKey()]!!)
        assert(orderBook.numOrders == 3)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testSingleTradeEqualPricesOrdersAskFirst() {

        val orderBook = OrderBook()
        val ask = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order(price = 111, quantity = 9, type = OrderType.BID)
        orderBook.processLimitOrder(ask)
        orderBook.processLimitOrder(bid)

        assert(ask.isFulfilled())
        assert(bid.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 1)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.quantity == 0)
        assert(bid.quantity == 0)
        assert(bid.orderTrades[0] == ask.orderTrades[0] && bid.orderTrades[0] == orderBook.trades[0])
        assert(bid.orderTrades[0].quantity == 9)
        assert(bid.orderTrades[0].buyer == bid)
        assert(bid.orderTrades[0].seller == ask)
        assert(bid.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testSingleTradeEqualPricesOrdersBidFirst() {
        val orderBook = OrderBook()
        val ask = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order(price = 111, quantity = 9, type = OrderType.BID)
        orderBook.processLimitOrder(bid)
        orderBook.processLimitOrder(ask)

        assert(ask.isFulfilled())
        assert(bid.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 1)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.quantity == 0)
        assert(bid.quantity == 0)
        assert(bid.orderTrades[0] == ask.orderTrades[0] && bid.orderTrades[0] == orderBook.trades[0])
        assert(bid.orderTrades[0].quantity == 9)
        assert(bid.orderTrades[0].buyer == bid)
        assert(bid.orderTrades[0].seller == ask)
        assert(bid.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testMultipleTradeEqualPricesOrdersAsksFirst() {

        val orderBook = OrderBook()

        val ask1 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask2 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask3 = Order(price = 111, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 111, quantity = 9, type = OrderType.BID)
        val bid2 = Order(price = 111, quantity = 9, type = OrderType.BID)
        val bid3 = Order(price = 111, quantity = 9, type = OrderType.BID)

        orderBook.processLimitOrder(ask1)
        orderBook.processLimitOrder(ask2)
        orderBook.processLimitOrder(ask3)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        assert(ask1.isFulfilled())
        assert(ask2.isFulfilled())
        assert(ask3.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 6)

        assert(ask1.quantity == 0)
        assert(ask2.quantity == 0)
        assert(ask3.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid1.orderTrades[0] == ask1.orderTrades[0])
        assert(bid2.orderTrades[0] == ask2.orderTrades[0])
        assert(bid3.orderTrades[0] == ask3.orderTrades[0])
        assert(bid1.orderTrades[0].quantity == 9)
        assert(bid2.orderTrades[0].quantity == 9)
        assert(bid3.orderTrades[0].quantity == 9)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask1)
        assert(bid2.orderTrades[0].seller == ask2)
        assert(bid3.orderTrades[0].seller == ask3)
        assert(bid1.orderTrades[0].taker == OrderType.BID)
        assert(bid2.orderTrades[0].taker == OrderType.BID)
        assert(bid3.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testMultipleTradeEqualPricesOrdersBidsFirst() {

        val orderBook = OrderBook()

        val ask1 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask2 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask3 = Order(price = 111, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 111, quantity = 9, type = OrderType.BID)
        val bid2 = Order(price = 111, quantity = 9, type = OrderType.BID)
        val bid3 = Order(price = 111, quantity = 9, type = OrderType.BID)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        orderBook.processLimitOrder(ask1)
        orderBook.processLimitOrder(ask2)
        orderBook.processLimitOrder(ask3)

        assert(ask1.isFulfilled())
        assert(ask2.isFulfilled())
        assert(ask3.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 6)

        assert(ask1.quantity == 0)
        assert(ask2.quantity == 0)
        assert(ask3.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid1.orderTrades[0] == ask1.orderTrades[0] && bid1.orderTrades[0] == orderBook.trades[0])
        assert(bid2.orderTrades[0] == ask2.orderTrades[0] && bid2.orderTrades[0] == orderBook.trades[1])
        assert(bid3.orderTrades[0] == ask3.orderTrades[0] && bid3.orderTrades[0] == orderBook.trades[2])
        assert(bid1.orderTrades[0].quantity == 9)
        assert(bid2.orderTrades[0].quantity == 9)
        assert(bid3.orderTrades[0].quantity == 9)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask1)
        assert(bid2.orderTrades[0].seller == ask2)
        assert(bid3.orderTrades[0].seller == ask3)
        assert(bid1.orderTrades[0].taker == OrderType.ASK)
        assert(bid2.orderTrades[0].taker == OrderType.ASK)
        assert(bid3.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testSingleTradeHigherBidQuantityEqualPricesBidFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order(price = 111, quantity = 6, type = OrderType.BID)

        orderBook.processLimitOrder(bid)
        orderBook.processLimitOrder(ask)

        assert(ask.isNotFulfilled())
        assert(bid.isFulfilled())

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 1)
        assert(orderBook.listPriceMap.size == 1)
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.quantity == 3)
        assert(bid.quantity == 0)
        assert(bid.orderTrades[0] == ask.orderTrades[0] && bid.orderTrades[0] == orderBook.trades[0])
        assert(bid.orderTrades[0].quantity == 6)
        assert(bid.orderTrades[0].buyer == bid)
        assert(bid.orderTrades[0].seller == ask)
        assert(bid.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testSingleTradeHigherBidQuantityEqualPricesAskFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order(price = 111, quantity = 6, type = OrderType.BID)

        orderBook.processLimitOrder(ask)
        orderBook.processLimitOrder(bid)

        assert(ask.isNotFulfilled())
        assert(bid.isFulfilled())

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 1)
        assert(orderBook.listPriceMap.size == 1)
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.quantity == 3)
        assert(bid.quantity == 0)
        assert(bid.orderTrades[0] == ask.orderTrades[0] && bid.orderTrades[0] == orderBook.trades[0])
        assert(bid.orderTrades[0].quantity == 6)
        assert(bid.orderTrades[0].buyer == bid)
        assert(bid.orderTrades[0].seller == ask)
        assert(bid.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testSingleTradeHigherBidQuantityDiffPricesBidFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val bid = Order(price = 115, quantity = 6, type = OrderType.BID)

        orderBook.processLimitOrder(bid)
        orderBook.processLimitOrder(ask)

        assert(ask.isNotFulfilled())
        assert(bid.isFulfilled())

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.size == 1)
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 1)
        assert(orderBook.listPriceMap.size == 1)
        assert(orderBook.sequence == 1)
        assert(orderBook.numOrders == 2)

        assert(ask.quantity == 3)
        assert(bid.quantity == 0)
        assert(bid.orderTrades[0] == ask.orderTrades[0] && bid.orderTrades[0] == orderBook.trades[0])
        assert(bid.orderTrades[0].quantity == 6)
        assert(bid.orderTrades[0].buyer == bid)
        assert(bid.orderTrades[0].seller == ask)
        assert(bid.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testMultipleTradesDiffPricesOrdersBidsFirst() {

        val orderBook = OrderBook()

        val ask1 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask2 = Order(price = 113, quantity = 9, type = OrderType.ASK)
        val ask3 = Order(price = 112, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 9, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 9, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 9, type = OrderType.BID)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        orderBook.processLimitOrder(ask1)
        orderBook.processLimitOrder(ask2)
        orderBook.processLimitOrder(ask3)

        assert(ask1.isFulfilled())
        assert(ask2.isFulfilled())
        assert(ask3.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 6)

        assert(ask1.quantity == 0)
        assert(ask2.quantity == 0)
        assert(ask3.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid2.orderTrades[0] == ask1.orderTrades[0])
        assert(bid3.orderTrades[0] == ask2.orderTrades[0])
        assert(bid1.orderTrades[0] == ask3.orderTrades[0])
        assert(bid1.orderTrades[0].quantity == 9)
        assert(bid2.orderTrades[0].quantity == 9)
        assert(bid3.orderTrades[0].quantity == 9)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask3)
        assert(bid3.orderTrades[0].seller == ask2)
        assert(bid2.orderTrades[0].seller == ask1)
        assert(bid1.orderTrades[0].taker == OrderType.ASK)
        assert(bid2.orderTrades[0].taker == OrderType.ASK)
        assert(bid3.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testMultipleTradesDiffPricesOrdersAsksFirst() {

        val orderBook = OrderBook()

        val ask1 = Order(price = 111, quantity = 9, type = OrderType.ASK)
        val ask2 = Order(price = 113, quantity = 9, type = OrderType.ASK)
        val ask3 = Order(price = 112, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 9, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 9, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 9, type = OrderType.BID)

        orderBook.processLimitOrder(ask1)
        orderBook.processLimitOrder(ask2)
        orderBook.processLimitOrder(ask3)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        assert(ask1.isFulfilled())
        assert(ask2.isFulfilled())
        assert(ask3.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 6)

        assert(ask1.quantity == 0)
        assert(ask2.quantity == 0)
        assert(ask3.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid2.orderTrades[0] == ask3.orderTrades[0])
        assert(bid3.orderTrades[0] == ask2.orderTrades[0])
        assert(bid1.orderTrades[0] == ask1.orderTrades[0])
        assert(bid1.orderTrades[0].quantity == 9)
        assert(bid2.orderTrades[0].quantity == 9)
        assert(bid3.orderTrades[0].quantity == 9)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid2.orderTrades[0].seller == ask3)
        assert(bid3.orderTrades[0].seller == ask2)
        assert(bid1.orderTrades[0].seller == ask1)
        assert(bid1.orderTrades[0].taker == OrderType.BID)
        assert(bid2.orderTrades[0].taker == OrderType.BID)
        assert(bid3.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeAskWithSmallAsksTradeNoQuantityLeftOverAsksFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 112, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 3, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 4, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 2, type = OrderType.BID)

        orderBook.processLimitOrder(ask)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        assert(ask.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(ask.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid1.orderTrades[0] == ask.orderTrades[0])
        assert(bid2.orderTrades[0] == ask.orderTrades[1])
        assert(bid3.orderTrades[0] == ask.orderTrades[2])
        assert(bid1.orderTrades[0].quantity == 3)
        assert(bid2.orderTrades[0].quantity == 4)
        assert(bid3.orderTrades[0].quantity == 2)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask)
        assert(bid2.orderTrades[0].seller == ask)
        assert(bid3.orderTrades[0].seller == ask)
        assert(bid1.orderTrades[0].taker == OrderType.BID)
        assert(bid2.orderTrades[0].taker == OrderType.BID)
        assert(bid3.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeBidWithSmallAsksTradeNoQuantityLeftOverBidFirst() {

        val orderBook = OrderBook()

        val ask1 = Order(price = 112, quantity = 2, type = OrderType.ASK)
        val ask2 = Order(price = 110, quantity = 3, type = OrderType.ASK)
        val ask3 = Order(price = 111, quantity = 4, type = OrderType.ASK)

        val bid = Order(price = 115, quantity = 9, type = OrderType.BID)

        orderBook.processLimitOrder(bid)

        orderBook.processLimitOrder(ask1)
        orderBook.processLimitOrder(ask2)
        orderBook.processLimitOrder(ask3)

        assert(bid.isFulfilled())
        assert(ask1.isFulfilled())
        assert(ask2.isFulfilled())
        assert(ask3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(bid.quantity == 0)
        assert(ask1.quantity == 0)
        assert(ask2.quantity == 0)
        assert(ask3.quantity == 0)
        assert(ask1.orderTrades[0] == bid.orderTrades[0])
        assert(ask2.orderTrades[0] == bid.orderTrades[1])
        assert(ask3.orderTrades[0] == bid.orderTrades[2])
        assert(ask1.orderTrades[0].quantity == 2)
        assert(ask2.orderTrades[0].quantity == 3)
        assert(ask3.orderTrades[0].quantity == 4)
        assert(ask1.orderTrades[0].buyer == bid)
        assert(ask2.orderTrades[0].buyer == bid)
        assert(ask3.orderTrades[0].buyer == bid)
        assert(ask1.orderTrades[0].seller == ask1)
        assert(ask2.orderTrades[0].seller == ask2)
        assert(ask3.orderTrades[0].seller == ask3)
        assert(ask1.orderTrades[0].taker == OrderType.ASK)
        assert(ask2.orderTrades[0].taker == OrderType.ASK)
        assert(ask3.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeAskWithSmallAsksTradeNoQuantityLeftOverBidsFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 112, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 3, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 4, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 2, type = OrderType.BID)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        orderBook.processLimitOrder(ask)

        assert(ask.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(ask.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid2.orderTrades[0] == ask.orderTrades[0])
        assert(bid3.orderTrades[0] == ask.orderTrades[1])
        assert(bid1.orderTrades[0] == ask.orderTrades[2])
        assert(bid1.orderTrades[0].quantity == 3)
        assert(bid2.orderTrades[0].quantity == 4)
        assert(bid3.orderTrades[0].quantity == 2)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask)
        assert(bid2.orderTrades[0].seller == ask)
        assert(bid3.orderTrades[0].seller == ask)
        assert(bid1.orderTrades[0].taker == OrderType.ASK)
        assert(bid2.orderTrades[0].taker == OrderType.ASK)
        assert(bid3.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeAskWithSmallAsksTradeNoQuantityLeftOverAskFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 112, quantity = 9, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 3, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 4, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 2, type = OrderType.BID)

        orderBook.processLimitOrder(ask)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        assert(ask.isFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(orderBook.isEmpty())
        assert(orderBook.asks.isEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(ask.quantity == 0)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid1.orderTrades[0] == ask.orderTrades[0])
        assert(bid2.orderTrades[0] == ask.orderTrades[1])
        assert(bid3.orderTrades[0] == ask.orderTrades[2])
        assert(bid1.orderTrades[0].quantity == 3)
        assert(bid2.orderTrades[0].quantity == 4)
        assert(bid3.orderTrades[0].quantity == 2)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask)
        assert(bid2.orderTrades[0].seller == ask)
        assert(bid3.orderTrades[0].seller == ask)
        assert(bid1.orderTrades[0].taker == OrderType.BID)
        assert(bid2.orderTrades[0].taker == OrderType.BID)
        assert(bid3.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeAskWithSmallAsksTradeWithQuantityLeftOverBidsFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 112, quantity = 12, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 3, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 4, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 2, type = OrderType.BID)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        orderBook.processLimitOrder(ask)

        assert(ask.isNotFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(!orderBook.isEmpty())
        assert(orderBook.asks.isNotEmpty())
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap.isNotEmpty())
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(ask.quantity == 3)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid2.orderTrades[0] == ask.orderTrades[0])
        assert(bid3.orderTrades[0] == ask.orderTrades[1])
        assert(bid1.orderTrades[0] == ask.orderTrades[2])
        assert(bid1.orderTrades[0].quantity == 3)
        assert(bid2.orderTrades[0].quantity == 4)
        assert(bid3.orderTrades[0].quantity == 2)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask)
        assert(bid2.orderTrades[0].seller == ask)
        assert(bid3.orderTrades[0].seller == ask)
        assert(bid1.orderTrades[0].taker == OrderType.ASK)
        assert(bid2.orderTrades[0].taker == OrderType.ASK)
        assert(bid3.orderTrades[0].taker == OrderType.ASK)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

    @Test
    fun testLargeAskWithSmallAsksTradeWithQuantityLeftOverAskFirst() {

        val orderBook = OrderBook()

        val ask = Order(price = 112, quantity = 12, type = OrderType.ASK)

        val bid1 = Order(price = 115, quantity = 3, type = OrderType.BID)
        val bid2 = Order(price = 117, quantity = 4, type = OrderType.BID)
        val bid3 = Order(price = 116, quantity = 2, type = OrderType.BID)

        orderBook.processLimitOrder(ask)

        orderBook.processLimitOrder(bid1)
        orderBook.processLimitOrder(bid2)
        orderBook.processLimitOrder(bid3)

        assert(ask.isNotFulfilled())
        assert(bid1.isFulfilled())
        assert(bid2.isFulfilled())
        assert(bid3.isFulfilled())

        assert(!orderBook.isEmpty())
        assert(orderBook.asks[orderBook.asks.firstKey()]!!.price == 112)
        assert(orderBook.bids.isEmpty())
        assert(orderBook.trades.size == 3)
        assert(orderBook.listPriceMap[112] == orderBook.asks[orderBook.asks.firstKey()]!!)
        assert(orderBook.sequence == 3)
        assert(orderBook.numOrders == 4)

        assert(ask.quantity == 3)
        assert(bid1.quantity == 0)
        assert(bid2.quantity == 0)
        assert(bid3.quantity == 0)
        assert(bid1.orderTrades[0] == ask.orderTrades[0])
        assert(bid2.orderTrades[0] == ask.orderTrades[1])
        assert(bid3.orderTrades[0] == ask.orderTrades[2])
        assert(bid1.orderTrades[0].quantity == 3)
        assert(bid2.orderTrades[0].quantity == 4)
        assert(bid3.orderTrades[0].quantity == 2)
        assert(bid1.orderTrades[0].buyer == bid1)
        assert(bid2.orderTrades[0].buyer == bid2)
        assert(bid3.orderTrades[0].buyer == bid3)
        assert(bid1.orderTrades[0].seller == ask)
        assert(bid2.orderTrades[0].seller == ask)
        assert(bid3.orderTrades[0].seller == ask)
        assert(bid1.orderTrades[0].taker == OrderType.BID)
        assert(bid2.orderTrades[0].taker == OrderType.BID)
        assert(bid3.orderTrades[0].taker == OrderType.BID)

        assert(Util.isJSONValid(orderBook.getOrderBookJSON()))
        assert(Util.isJSONValid(orderBook.getRecentTrades()))
    }

}