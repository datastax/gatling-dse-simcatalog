package feeds.examples.cql

import java.nio.ByteBuffer
import java.security.MessageDigest

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging


class UrlFeed extends BaseFeed with LazyLogging {

  def write = {
    def rowData = getRowData

    // optionally log out the data that was generated for future use
    // logger.info("{},{},{}", Array(rowData.get("order_no"), rowData.get("cust_id"), rowData.get("email")))

    Iterator.continually(rowData)
  }

  def getRowData = {

    val md = MessageDigest.getInstance("SHA-1")

    val url = faker.internet.url
    val orsp_hash = md.digest(url.getBytes("UTF-8"))
    val url_hash = orsp_hash
    val timestamp = getRandomEpoch // System.currentTimeMillis

    Map(
      "key" -> ByteBuffer.wrap(url_hash),
      "automation" -> true,
      "namespace" -> "mynamespace",
      "name" -> faker.bothify("SAMPLETHING##"),
      "value" -> faker.shakespeare().asYouLikeItQuote(),
      "timestamp" -> timestamp,
      "update_ts" -> timestamp,
      "create_ts" -> timestamp,
      "change_ts" -> timestamp,
      "orsp_hash" -> orsp_hash,
      "orsp_hash_dot" -> orsp_hash.map("%02x".format(_)).mkString
    )
  }

}