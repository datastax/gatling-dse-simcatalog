package sims.examples.graph

import actions.examples.graph.GraphActions
import com.datastax.gatling.plugin.GraphPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.examples.graph.GraphFeed
import io.gatling.core.Predef._

class WriteSimulation extends BaseSimulation {

  val simName = "graphExamples"
  val scenarioName = "writeOnly"

  val simConf = new SimConfig(conf, simName, scenarioName)

  // set the graph name to match configs
  val graphName = simConf.getSimulationConfStr("graphName")
  cass.getCluster.getConfiguration.getGraphOptions.setGraphName(graphName)

  val graphActions = new GraphActions(cass.getSession)
  graphActions.initGraphSchema

  val graphFeed = new GraphFeed

  val writeScenario = scenario("GraphWrite")
      .feed(graphFeed.getBeerDrinker)
      .feed(graphFeed.getBeerStyle)
      .exec(graphActions.insertBeerDrinker)

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(graphProtocol)

}


