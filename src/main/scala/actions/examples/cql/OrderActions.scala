package actions.examples.cql

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import io.gatling.core.Predef._

/**
  * Order Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConf
  */
class OrderActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  // create keyspace/table if they do not exist
  createKeyspace
  createTables()

  // A regular string query can be used as well as the QueryBuilder
  private val writeOrderQuery = QueryBuilder.insertInto(keyspace, table)
      .value("order_no", raw("?"))
      .value("alt_fname", raw("?"))
      .value("alt_lname", raw("?"))
      .value("city", raw("?"))
      .value("cust_id", raw("?"))
      .value("data", raw("?"))
      .value("email", raw("?"))
      .value("esd", raw("?"))
      .value("fname", raw("?"))
      .value("fulfill_type", raw("?"))
      .value("group_no", raw("?"))
      .value("hold_status", raw("?"))
      .value("hold_type", raw("?"))
      .value("item_id", raw("?"))
      .value("line_code", raw("?"))
      .value("line_status", raw("?"))
      .value("lname", raw("?"))
      .value("modified_dt", raw("?"))
      .value("offer_id", raw("?"))
      .value("opd", raw("?"))
      .value("order_date", raw("?"))
      .value("order_type", raw("?"))
      .value("pallet_asn", raw("?"))
      .value("partner_item_id", raw("?"))
      .value("phone", raw("?"))
      .value("pi_hash", raw("?"))
      .value("pkg_asn", raw("?"))
      .value("po_line_code", raw("?"))
      .value("po_line_status", raw("?"))
      .value("po_no", raw("?"))
      .value("rma", raw("?"))
      .value("seller_id", raw("?"))
      .value("shard_id", raw("?"))
      .value("ship_method", raw("?"))
      .value("ship_node", raw("?"))
      .value("source", raw("?"))
      .value("state", raw("?"))
      .value("store_id", raw("?"))
      .value("store_tc_no", raw("?"))
      .value("tc_no", raw("?"))
      .value("tracking_no", raw("?"))
      .value("upc", raw("?"))


  def writeOrder = {

    val preparedStatement = session.prepare(writeOrderQuery)

    group(Groups.INSERT) {
      exec(cql("Order")
          .executePrepared(preparedStatement)
          .withParams(
            "${order_no}",
            "${alt_fname}",
            "${alt_lname}",
            "${city}",
            "${cust_id}",
            "${data}",
            "${email}",
            "${esd}",
            "${fname}",
            "${fulfill_type}",
            "${group_no}",
            "${hold_status}",
            "${hold_type}",
            "${item_id}",
            "${line_code}",
            "${line_status}",
            "${lname}",
            "${modified_dt}",
            "${offer_id}",
            "${opd}",
            "${order_date}",
            "${order_type}",
            "${pallet_asn}",
            "${partner_item_id}",
            "${phone}",
            "${pi_hash}",
            "${pkg_asn}",
            "${po_line_code}",
            "${po_line_status}",
            "${po_no}",
            "${rma}",
            "${seller_id}",
            "${shard_id}",
            "${ship_method}",
            "${ship_node}",
            "${source}",
            "${state}",
            "${store_id}",
            "${store_tc_no}",
            "${tc_no}",
            "${tracking_no}",
            "${upc}"
          )
          .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM) // ConsistencyLevel can be set per query
          .check(rowCount is 0) // an insert should not return rows
      )
    }
  }


  def writeOrderWithLwt = {

    val query = writeOrderQuery.ifNotExists()
    val preparedStatement2 = session.prepare(query)

    group(Groups.INSERT) {
      exec(cql("OrderLwt")
          .executePrepared(preparedStatement2)
          .withParams(
            "${order_no}",
            "${alt_fname}",
            "${alt_lname}",
            "${city}",
            "${cust_id}",
            "${data}",
            "${email}",
            "${esd}",
            "${fname}",
            "${fulfill_type}",
            "${group_no}",
            "${hold_status}",
            "${hold_type}",
            "${item_id}",
            "${line_code}",
            "${line_status}",
            "${lname}",
            "${modified_dt}",
            "${offer_id}",
            "${opd}",
            "${order_date}",
            "${order_type}",
            "${pallet_asn}",
            "${partner_item_id}",
            "${phone}",
            "${pi_hash}",
            "${pkg_asn}",
            "${po_line_code}",
            "${po_line_status}",
            "${po_no}",
            "${rma}",
            "${seller_id}",
            "${shard_id}",
            "${ship_method}",
            "${ship_node}",
            "${source}",
            "${state}",
            "${store_id}",
            "${store_tc_no}",
            "${tc_no}",
            "${tracking_no}",
            "${upc}"
          )
          .consistencyLevel(ConsistencyLevel.LOCAL_SERIAL) // Consitency of LWT can also be set
          .check(columnValue("[applied]") is true) // since this uses LWT we want to make sure that it succeeded
      )
    }
  }


  val readOrderQuery = QueryBuilder.select().from(keyspace, table).where(QueryBuilder.eq("order_no", raw("?")))


  def readOrder = {

    val preparedStatement = session.prepare(readOrderQuery)

    group(Groups.SELECT) {
      exec(cql("Order")
          .executePrepared(preparedStatement)
          .withParams(
            "${order_no}"
          )
          .check(rowCount greaterThan 0)
      )
    }
  }


  def readOrderAndSaveToSession = {

    val preparedStatement = session.prepare(readOrderQuery)

    group(Groups.SELECT) {
      exec(cql("Order2")
          .executePrepared(preparedStatement)
          .withParams(
            "${order_no}"
          )
          .check(rowCount greaterThan 0)

          // This will save the value of col "email" to a "testParam" in the current session
          // This can be used like a feed in the withParams("$testParam") for reuse
          .check(columnValue("email").find.saveAs("testParam"))

      ).exitHereIfFailed // Print out for debugging
          .exec { session =>
        println(session)
        session
      }
    }
  }


  def createTables(): Unit = {

    runQueries(Array(

      s"CREATE TABLE IF NOT EXISTS $keyspace.$table ( order_no text, alt_fname text, alt_lname text, " +
          s"city text, cust_id text, data text, email text, esd set<timestamp>, " +
          s"fname text, fulfill_type text, group_no text, hold_status text, hold_type text, item_id text, line_code text, " +
          s"line_status text, lname text, modified_dt timestamp, offer_id text, opd set<timestamp>, order_date timestamp, " +
          s"order_type text, pallet_asn text, partner_item_id text, phone text, pi_hash text, pkg_asn text, " +
          s"po_line_code text, po_line_status text, po_no text, rma set<text>, seller_id text, shard_id text, ship_method text, " +
          s"ship_node text, source text, state text, store_id text, store_tc_no text, tc_no text, " +
          s"tracking_no text, upc text, PRIMARY KEY (order_no))"

    ))

  }

}
