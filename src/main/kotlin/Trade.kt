import java.util.*

class Trade(var buyer: Order, var seller: Order, var quantity: Int) {
    var timestamp = Calendar.getInstance().time

}
