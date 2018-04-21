package feeds.examples.cql

import java.util.Date

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging

import scala.collection.JavaConverters._


class OrderFeed extends BaseFeed with LazyLogging {

  def write = {
    def rowData = getRowData

    // optionally log out the data that was generated for future use
    // logger.info("{},{},{}", Array(rowData.get("order_no"), rowData.get("cust_id"), rowData.get("email")))

    Iterator.continually(rowData)
  }


  def getRowData = {
    Map(
      "order_no" -> faker.bothify("###########"), //F2AC45A479F04796A584DD9FCE751842
      "alt_fname" -> faker.name.firstName,
      "alt_lname" -> faker.name.lastName,
      "city" -> faker.address.city,
      "cust_id" -> java.util.UUID.randomUUID.toString,
      "data" -> jsonDataGenerator.generate(12),
      "email" -> faker.internet.emailAddress,
      "esd" -> Set[Date](getRandomEpoch, getRandomEpoch).asJava, // set timestamp
      "fname" -> faker.name.firstName,
      "fulfill_type" -> faker.bothify("?#?"),
      "group_no" -> faker.numerify("##############"),
      "hold_status" -> "",
      "hold_type" -> "",
      "item_id" -> faker.numerify("#########"),
      "line_code" -> faker.numerify("####"),
      "line_status" -> "DELIVERED",
      "lname" -> faker.name.lastName,
      "modified_dt" -> getRandomEpoch,
      "offer_id" -> "",
      "opd" -> Set[Date](getRandomEpoch, getRandomEpoch).asJava, // set timestamp
      "order_date" -> getRandomEpoch,
      "order_type" -> "DOMESTIC",
      "pallet_asn" -> "",
      "partner_item_id" -> "",
      "phone" -> faker.phoneNumber.phoneNumber,
      "pi_hash" -> "",
      "pkg_asn" -> "",
      "po_line_code" -> "",
      "po_line_status" -> "SHIPPED",
      "po_no" -> faker.numerify("####"),
      "rma" -> Set[String](faker.bothify("???????"), faker.bothify("???????")).asJava, // set text
      "seller_id" -> "0",
      "shard_id" -> faker.numerify("####"),
      "ship_method" -> "VALUE",
      "ship_node" -> faker.numerify("####"),
      "source" -> faker.bothify("???_???"),
      "state" -> faker.address().state(),
      "store_id" -> faker.bothify("?#???##"),
      "store_tc_no" -> "",
      "tc_no" -> faker.numerify("####"),
      "tracking_no" -> faker.numerify("############"),
      "upc" -> getUpc
    )
  }
}
