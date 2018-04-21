package feeds.examples.graph

import com.datastax.gatling.stress.core.BaseFeed
import com.typesafe.scalalogging.LazyLogging


class GraphFeed extends BaseFeed with LazyLogging {

  def getBeerDrinker = {
    Iterator.continually(Map(
      "firstName" -> faker.name().firstName(),
      "lastName" -> faker.name().lastName(),
      "beer" -> faker.beer().name()
    ))
  }

  def getBeerStyle = {
    Iterator.continually(Map(
      "style" -> faker.beer().style()
    ))
  }

  def getRandomBeerStyle = {
    Iterator.continually(Map(
      "beer" -> getRandom(Array("Duvel", "HopSlam Ale", "Orval Trappist Ale", "Maudite"))
    ))
  }

}
