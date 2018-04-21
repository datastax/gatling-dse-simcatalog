package sims.examples.cql.orders

import actions.examples.cql.OrderActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.{FetchBaseData, SimConfig}
import feeds.examples.cql.OrderFeed
import io.gatling.core.Predef._

class ReadWriteOrderPercentSimulation extends BaseSimulation {

  val simName = "examples"

  /**
    * Start Write Scenario Setup
    */
  // load conf based on the simName and scenarioName from application.conf for writeOrderPercent
  val simConfWrite = new SimConfig(conf, simName, "writeOrderPercent")

  // init orderWriteActions aka queries
  val orderWriteActions = new OrderActions(cass, simConfWrite)

  // Load feed for generating data
  val orderWriteFeed = new OrderFeed().write

  // build scenario to run with feed and write action
  val writeScenario = scenario("OrderWrite")
      .feed(orderWriteFeed)
      .exec(orderWriteActions.writeOrder)

  /**
    * End Write Scenario Setup
    */


  /**
    * Start Read Simulation
    */
  // load conf based on the simName and scenarioName from application.conf for writeOrderPercent
  val simConfRead = new SimConfig(conf, simName, "writeOrderPercent")

  // init orderReadActions aka queries
  val orderReadActions = new OrderActions(cass, simConfRead)

  // create base data file using config values
  new FetchBaseData(simConfRead, cass).createBaseDataCsv()

  val csvFeeder = csv(getDataPath(simConfRead)).random

  val readScenario = scenario("OrderRead")
      .feed(csvFeeder)
      .exec(orderReadActions.readOrder)
  /**
    * End Read Scenario Setup
    */


  // setup the traffic to run w/ the scenario
  setUp(

    // Both of the below will read the userConstantCount from examples.default
    // and find the percentage from each scenario section and auto-calculate the amount
    // of users to use for each scenarios load.
    // Both scenarios will be run at the same time asynchronously
    loadGenerator.rampUpToPercentage(writeScenario, simConfWrite),
    loadGenerator.rampUpToPercentage(writeScenario, simConfRead)

  ).protocols(cqlProtocol)

}