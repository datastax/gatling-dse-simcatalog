package sims.examples.cql.urls

import actions.examples.cql.UrlActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.examples.cql.UrlFeed
import io.gatling.core.Predef._

class InsertUrlsSimulation extends BaseSimulation {

  val simName = "examples"
  val scenarioName = "insertUrls"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val urlActions = new UrlActions(cass, simConf)

  val writeFeed = new UrlFeed

  val writeScenario = scenario("Insert_URLs_with_LWT")
      .feed(writeFeed.write)
      .exec(urlActions.writeUrlsBatch)
      .doIf(session => session("batch_applied").as[Boolean].equals(true)) {
        exec(urlActions.getByUrlHash)
      }

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(cqlProtocol)
}