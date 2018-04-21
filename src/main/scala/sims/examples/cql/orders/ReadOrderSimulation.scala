package sims.examples.cql.orders

import actions.examples.cql.OrderActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import io.gatling.core.Predef._

class ReadOrderSimulation extends BaseSimulation {

  val simName = "examples"
  val scenarioName = "readOrder"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val orderActions = new OrderActions(cass, simConf)

  // create base data file using config values
  new FetchBaseData(simConf, cass).createBaseDataCsv()

  val feederFile = getDataPath(simConf)
  val csvFeeder = csv(feederFile).random

  val readScenario = scenario("OrderRead")
      .feed(csvFeeder)
      .exec(orderActions.readOrder)

  setUp(

    loadGenerator.rampUpToConstant(readScenario, simConf)

  ).protocols(cqlConfig)
}