import java.util.*

fun main() {

    var l1 = ListOrdersAtPrice(10)
    var l2 = ListOrdersAtPrice(11)
    var l3 = ListOrdersAtPrice(12)
    var l4 = ListOrdersAtPrice(13)

    var qu = PriorityQueue<ListOrdersAtPrice>()
    var qr = PriorityQueue<ListOrdersAtPrice>(Collections.reverseOrder())

    qu.add( l3 )
    qu.add( l2 )
    qu.add( l1 )
    qu.add( l4 )

    qr.add( l3 )
    qr.add( l2 )
    qr.add( l1 )
    qr.add( l4 )

    println(qu.poll())
    println(qu.poll())
    println(qu.poll())
    println(qu.poll())
    println(qr.poll())
    println(qr.poll())
    println(qr.poll())
    println(qr.poll())
}