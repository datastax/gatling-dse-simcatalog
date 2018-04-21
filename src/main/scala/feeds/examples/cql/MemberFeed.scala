package feeds.examples.cql

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging

class MemberFeed extends BaseFeed with LazyLogging {


  def getMember = {
    def rowData = this.getMemberRow

    // optionally log out the data that was generated for future use
    // logger.info("{},{},{}", Array(rowData.get("order_no"), rowData.get("cust_id"), rowData.get("email")))

    Iterator.continually(rowData)
  }

  private def getMemberRow = {
    Map(
      "member_uuid" -> getUuid,
      "first_name" -> faker.name.firstName,
      "last_name" -> faker.name.lastName,
      "email" -> faker.internet.emailAddress,
      "age" -> faker.number.numberBetween(10, 65),
      "created_date" -> getRandomEpoch
    )
  }

  def getMemberAddress = {
    def rowData = this.getMemberAddressRow

    // optionally log out the data that was generated for future use
    // logger.info("{},{},{}", Array(rowData.get("order_no"), rowData.get("cust_id"), rowData.get("email")))

    Iterator.continually(rowData)
  }

  private def getMemberAddressRow = {
    Map(
      "address_uuid" -> getUuid,
      "street" -> faker.address.streetAddress(true),
      "city" -> faker.address.city,
      "state" -> faker.address.stateAbbr,
      "zip" -> faker.address.zipCode,
      "type" -> getRandom(Array("work", "home"))
    )
  }
}
