package sims

import io.gatling.app.Gatling
import io.gatling.core.config.GatlingPropertiesBuilder
import org.scalatest.{FlatSpec, Ignore}

@Ignore
class OrdersSimulationsSpec extends FlatSpec {

  val props = new GatlingPropertiesBuilder

  "WriteOrderSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.examples.cql.orders.WriteOrderSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

  "ReadOrderSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.examples.cql.orders.ReadOrderSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

  "ReadWriteOrderPercentSimulation" should "succeed with 0 failures" in {
    props.simulationClass("sims.examples.cql.orders.ReadWriteOrderPercentSimulation") //put your class name here
    assert(Gatling.fromMap(props.build).equals(0))
  }

}
