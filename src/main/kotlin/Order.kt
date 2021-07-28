import java.util.*

class Order(var price: Int, var quantity: Int, val type: OrderType){

    var timestamp = Calendar.getInstance().time
    var orderNum = UUID.randomUUID()
    // arraylist to store all trades that happen in the orderbook
    var orderTrades: ArrayList<Trade> = ArrayList<Trade>()

    fun isFullfilled(): Boolean{
        return quantity == 0
    }
}

