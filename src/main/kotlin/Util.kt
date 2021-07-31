import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.util.*

class Util {

    companion object {

        fun loadOrdersFromJsonFile(filePath: String): LinkedList<Order> {
            val inputStream: InputStream = File(filePath).inputStream()
            val inputString = inputStream.bufferedReader().use { it.readText() }
            return loadOrdersFromJsonString(inputString)
        }

        fun loadOrdersFromJsonString(inputString: String): LinkedList<Order> {
            val jsonArray = JSONArray(inputString)
//            println(jsonArray)
            val ordersList: LinkedList<Order> = LinkedList<Order>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                ordersList.add(
                    Order(
                        price = obj.getInt("price"),
                        quantity = obj.getInt("qty"),
                        type = if (obj.getString("side") == "bid") OrderType.BID else OrderType.ASK
                    )
                )
            }

            return ordersList
        }

        fun isJSONValid(test: String?): Boolean {
            try {
                JSONObject(test)
            } catch (ex: JSONException) {
                // e.g. in case JSONArray is valid as well...
                try {
                    JSONArray(test)
                } catch (ex1: JSONException) {
                    return false
                }
            }
            return true
        }
    }

}