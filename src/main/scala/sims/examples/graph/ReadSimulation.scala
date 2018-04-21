package sims.examples.graph

import actions.examples.graph.GraphActions
import com.datastax.gatling.plugin.GraphPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.examples.graph.GraphFeed
import io.gatling.core.Predef._

class ReadSimulation extends BaseSimulation {

  val simName = "graphExamples"
  val scenarioName = "readOnly"

  val simConf = new SimConfig(conf, simName, scenarioName)

  // set the graph name to match configs
  val graphName = simConf.getSimulationConfStr("graphName")
  cass.getCluster.getConfiguration.getGraphOptions.setGraphName(graphName)

  val graphActions = new GraphActions(cass.getSession)
  graphActions.initGraphSchema

  val graphFeed = new GraphFeed

  val readScenario = scenario("GraphRead")
      .feed(graphFeed.getBeerStyle)
      .feed(graphFeed.getRandomBeerStyle)
      .exec(graphActions.queryReadByStyleOfBeer)
      .exec(graphActions.queryReadDrinkersFavBeers)


  setUp(

    loadGenerator.rampUpToConstant(readScenario, simConf)

  ).protocols(graphProtocol)

}


