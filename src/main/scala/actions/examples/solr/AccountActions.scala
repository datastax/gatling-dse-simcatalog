package actions.examples.solr

import java.io.InputStream

import com.datastax.driver.core.{ConsistencyLevel, DataType}
import com.datastax.driver.core.querybuilder.QueryBuilder._
import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.driver.core.schemabuilder.SchemaBuilder
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseAction
import com.datastax.gatling.stress.libs.{Cassandra, SimConfig}
import com.mashape.unirest.http.Unirest
import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import org.json4s.DefaultFormats


/**
  * Account Actions
  *
  * @param cassandra Cassandra
  * @param simConf   SimConfig
  */
class AccountActions(cassandra: Cassandra, simConf: SimConfig) extends BaseAction(cassandra, simConf) {

  implicit val formats = DefaultFormats

  createKeyspace
  createTables
  createSolrSchema

  private val writeAccountQuery: Insert = QueryBuilder.insertInto(keyspace, table)
      .value("account_id", raw(":account_id"))
      .value("first_name", raw(":first_name"))
      .value("last_name", raw(":last_name"))
      .value("email", raw(":email"))
      .value("age", raw(":age"))
      .value("country_code", raw(":country_code"))
      .value("locale", raw(":locale"))
      .value("pass", raw(":pass"))
      .value("birthday", raw(":birthday"))
      .value("created_date", raw(":created_date"))
      .value("updated_date", raw(":updated_date"))


  private val writeMemberPreparedStatement = session.prepare(writeAccountQuery)

  def writeAccount: ChainBuilder = {

    exec(
      cql("InsertAccount")
          .executeNamed(writeMemberPreparedStatement)
    )

  }

  private val solrQuery = QueryBuilder.select().from(keyspace, table)
      .where(QueryBuilder.eq("solr_query", raw(":solr_query")))


  private val solrQueryPreparedStatement = session.prepare(solrQuery)

  def queryWithSolr = {
    exec(
      cql("SolrQuery")
          .executeNamed(solrQueryPreparedStatement)
          .consistencyLevel(ConsistencyLevel.LOCAL_ONE)
    )
  }


  private def createTables = {

    runQueries(Array(

      SchemaBuilder.createTable(keyspace, table)
          .addPartitionKey("account_id", DataType.uuid())
          .addColumn("first_name", DataType.text())
          .addColumn("last_name", DataType.text())
          .addColumn("email", DataType.text())
          .addColumn("country_code", DataType.text())
          .addColumn("locale", DataType.text())
          .addColumn("age", DataType.cint())
          .addColumn("birthday", DataType.date())
          .addColumn("pass", DataType.text())
          .addColumn("created_date", DataType.timestamp())
          .addColumn("updated_date", DataType.timestamp())
          .ifNotExists()
          .toString

    ))

  }


  private def createSolrSchema = {

    val solrConfig = simConf.getSimulationConf.getConfig("solr")
    val httpsBool = solrConfig.getBoolean("https")

    val urlBase = StringBuilder.newBuilder
    if (httpsBool) {
      urlBase.append("https")
    } else {
      urlBase.append("http")
    }

    urlBase.append("://")
    urlBase.append(simConf.getCassandraConf.getList("hosts").unwrapped().get(0))
    urlBase.append(":")
    urlBase.append(solrConfig.getInt("httpPort"))

    // URL: /solr/admin/cores?action=CREATE&name=$keyspace.$table
    val createCoreBase = urlBase.clone()
    createCoreBase.append("/solr/admin/cores")

    // URL: http://localhost:8983/solr/resource/keyspace.table/solrconfig.xml
    val createResourceBase = urlBase.clone()
    createResourceBase.append(s"/solr/resource/$keyspace.$table/")

    val stream_1 = getClass.getResourceAsStream(solrConfig.getString("configXml"))
    val solrConfigXml = scala.io.Source.fromInputStream(stream_1).mkString

    val stream_2: InputStream = getClass.getResourceAsStream(solrConfig.getString("schemaXml"))
    val solrSchemaXml = scala.io.Source.fromInputStream(stream_2).mkString

    val submitSolrConfigResource = Unirest.post(createResourceBase.toString() + "solrconfig.xml")
        .header("Content-type", "text/xml; charset=utf-8")
        .body(solrConfigXml)
        .asBinary()

    if (submitSolrConfigResource.getStatus != 200) {
      logger.error(s"Unable to submit solrconfig.xml file for core $keyspace.$table. " +
          s"Server Error: ${submitSolrConfigResource.getBody}")
      System.exit(1)
    }

    val submitSolrSchemaResource = Unirest.post(createResourceBase.toString() + "schema.xml")
        .header("Content-type", "text/xml; charset=utf-8")
        .body(solrSchemaXml)
        .asBinary()

    if (submitSolrSchemaResource.getStatus != 200) {
      logger.error(s"Unable to submit schema.xml file for core $keyspace.$table. " +
          s"Server Error: ${submitSolrSchemaResource.getBody}")
      System.exit(1)
    }

    val createCoreResponse = Unirest.post(createCoreBase.toString())
        .queryString("action", "CREATE")
        .queryString("name", s"$keyspace.$table")
        .queryString("reindex", "false")
        .asString()

    if (createCoreResponse.getStatus != 200) {

      if (createCoreResponse.getBody.contains("already exists and is loaded")) {
        logger.warn(s"Core for $keyspace.$table is already created, skipping.")
      } else {
        logger.error(s"Unable to submit CREATE core for $keyspace.$table. " +
            s"erver Error: ${createCoreResponse.getBody}")
        System.exit(1)
      }

    }


  }
}

