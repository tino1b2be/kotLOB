import java.util.*

class Order(var price: Int, var quantity: Int, type: OrderType){

    var timestamp = Calendar.getInstance().time
    var orderNum = UUID.randomUUID()
    var fullfilled = LinkedList<Order>()

    fun fulfillOrder( matchingOrders: LinkedList<Order>){
        TODO("add orders to fulfilled list")
    }
    fun ifFulfilled(): Boolean { return fullfilled.isEmpty() }

}

