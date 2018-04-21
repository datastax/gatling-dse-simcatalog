package actions.examples.cql

import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder

/**
  * Url Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConf
  */
class UrlActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  // create keyspace/table if they do not exist
  createKeyspace
  createTables()

  def writeUrlsBatch: ChainBuilder = {

    val batchStatement =
      s"""
         BEGIN BATCH
         insert into $keyspace.urls (key, automation, namespace, name, value, timestamp)
                values (:key, :automation, :namespace, :name, :value, :timestamp) IF NOT EXISTS;
         APPLY BATCH
      """

    val preparedBatch = session.prepare(batchStatement)

    group(Groups.INSERT) {
      exec(cql("UrlsBatch")
          .executeNamed(preparedBatch)
          // no need for defining params, auto-pulled from session
          .check(columnValue("[applied]") is true)
          .check(columnValue("[applied]").saveAs("batch_applied")) // save to the current Gatling session the value of the LWT query
      )
    }
  }

  /**
    * Get the row of the inserted key (note: this is just an example)
    *
    * @return
    */
  def getByUrlHash: ChainBuilder = {

    val query = s"select * from $keyspace.urls where key = ?"

    val preparedQuery = session.prepare(query)

    group(Groups.SELECT) {
      exec(cql("UrlsBatch")
          .executePrepared(preparedQuery)
          .withParams(List("key"))
          .check(rowCount greaterThan 0) // check rows returned is greater than 0
      )
    }
  }


  def createTables(): Unit = {

    runQueries(Array(

      s"CREATE TABLE IF NOT EXISTS $keyspace.urls (key blob, automation boolean, namespace text, name text, " +
          s"value text, timestamp timestamp, PRIMARY KEY ((key), automation, namespace, name));"

    ))

  }

}
