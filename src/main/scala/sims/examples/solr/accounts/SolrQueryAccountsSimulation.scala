package sims.examples.solr.accounts

import actions.examples.solr.AccountActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.examples.solr.AccountFeed
import io.gatling.core.Predef._

class SolrQueryAccountsSimulation extends BaseSimulation {

  val simName = "solrExamples"
  val scenarioName = "queryAccounts"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val actions = new AccountActions(cass, simConf)

  val accountFeed = new AccountFeed

  val writeScenario = scenario("SolrQuery")
      .feed(accountFeed.solrLocaleEnUs)
      .exec(actions.queryWithSolr)

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(cqlProtocol)
}