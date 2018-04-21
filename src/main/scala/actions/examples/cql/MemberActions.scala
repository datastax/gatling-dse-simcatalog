package actions.examples.cql

import com.datastax.driver.core.ConsistencyLevel
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

/**
  * Member Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConfig
  */
class MemberActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  private val memberTable = "members"
  private val memberAddressTable = "member_addresses"

  // create keyspace/table if they do not exist
  createKeyspace
  createTables()

  // A regular string query can be used as well as the QueryBuilder
  private val writeMemberQuery: Insert = QueryBuilder.insertInto(keyspace, memberTable)
      .value("member_uuid", raw(":member_uuid"))
      .value("first_name", raw(":first_name"))
      .value("last_name", raw(":last_name"))
      .value("email", raw(":email"))
      .value("age", raw(":age"))
      .value("created_date", raw(":created_date"))


  def writeMember: ChainBuilder = {

    val preparedStatement = session.prepare(writeMemberQuery)

    exec(cql("InsertMember")
        .executeNamed(preparedStatement)
        .consistencyLevel(ConsistencyLevel.LOCAL_QUORUM) // ConsistencyLevel can be set per query
        .check(rowCount.saveAs("cnt"))
        // an insert should not return rows
    ).exec { s: Session =>
      if (s.get("cnt").as[Int] < 1) {
        logger.error(preparedStatement.toString)
      }
      s
    }
  }


  private val writeMemberAddressQuery: Insert = QueryBuilder.insertInto(keyspace, memberAddressTable)
      .value("member_uuid", raw(":member_uuid"))
      .value("address_uuid", raw(":address_uuid"))
      .value("street", raw(":street"))
      .value("city", raw(":city"))
      .value("state", raw(":state"))
      .value("zip", raw(":zip"))
      .value("address_type", raw(":address_type"))
      .value("created_date", raw(":created_date"))


  def writeMemberAddresses: ChainBuilder = {

    val preparedStatement2 = session.prepare(writeMemberAddressQuery)

    exec(cql("InsertMemberAddresses")
        .executePrepared(preparedStatement2)
        .withParams(List("member_uuid", "address_uuid", "street", "city", "state", "zip", "type", "created_date"))
    )
  }


  def createTables(): Unit = {

    runQueries(Array(

      s"CREATE TABLE IF NOT EXISTS $keyspace.$memberTable (member_uuid UUID, " +
          s"first_name text, last_name text, email text, age int, created_date timestamp, PRIMARY KEY(member_uuid));",

      s"CREATE TABLE IF NOT EXISTS $keyspace.$memberAddressTable (member_uuid uuid, address_uuid uuid, street text, " +
          s"city text, state text, zip text, address_type text, created_date timestamp, PRIMARY KEY(member_uuid, address_uuid))"

    ))
  }

}
