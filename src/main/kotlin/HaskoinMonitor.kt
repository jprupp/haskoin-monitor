import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.stream.Stream

val mapper = ObjectMapper()

fun best(): JsonNode {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://btc.haskoin.com/api/block/best").build()
    val response = client.newCall(request).execute()
    return response.body()!!.let {
        mapper.readTree(it.byteStream())
    }
}

fun monitor(): Stream<JsonNode> {
    val client = OkHttpClient()
    val request = Request.Builder().url("https://btc.haskoin.com/api/events").build()
    val response = client.newCall(request).execute()
    val source = response.body()!!.byteStream()!!.bufferedReader().lines()!!
    return source.map { mapper.readTree(it) }
}

fun main(args: Array<String>) {
    println("best: " + best().get("height").asInt())
    monitor().use {
        it.forEach {
            println(it.get("type").asText() + ": " + it.get("id").asText())
        }
    }
}