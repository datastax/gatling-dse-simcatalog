package actions.examples.graph

import com.datastax.driver.dse.DseSession
import com.datastax.driver.dse.graph.SimpleGraphStatement
import com.datastax.gatling.plugin.GraphPredef._
import io.gatling.core.Predef._

class GraphActions(var session: DseSession) {

  /**
    * Insert Beer Drinker Vertex Using feeds
    *
    * @return
    */
  def insertBeerDrinker = {

    val simpleStatement = new SimpleGraphStatement("""g.addV(label, 'beerDrinker').properties("firstname", firstName)""")

    group("Write") {
      exec(graph("WriteBeerDrinker")
          .executeGraphStatement(simpleStatement)
          .withSetParams(Array("firstName"))
      )
    }
  }

  /**
    * Insert Beer Styles
    *
    * @return
    */
  def insertBeerStyles = {
    group("Write") {
      exec(graph("WriteBeerStyle")
          .executeGraph("""graph.addVertex(label, "beerStyle", "name", "${beer}", "style", "${style}")""")
      )
    }
  }


  def queryReadDrinkersFavBeers = {
    group("Read") {
      exec(graph("ReadDrinkersFavBeers")
          .executeGraph(
            """g.V().hasLabel("beerDrinker").has("favBeer", "${beer}")"""
          )
      )
    }
  }

  def queryReadByStyleOfBeer = {
    group("Read") {
      exec(graph("ReadByStyleOfBeer")
          .executeGraph(
            """g.V().has('style','${style}').hasLabel('beerDrinker')"""
          )
      )
    }
  }


  def initGraphSchema = {
    val schema =
      """
        |schema.propertyKey("name").Text().single().ifNotExists().create()
        |schema.propertyKey("style").Text().single().ifNotExists().create()
        |schema.propertyKey("firstName").Text().single().ifNotExists().create()
        |schema.propertyKey("lastName").Text().single().ifNotExists().create()
        |schema.propertyKey("favBeer").Text().single().ifNotExists().create()
        |schema.edgeLabel("likes").multiple().ifNotExists().create()
        |schema.vertexLabel("beerDrinker").properties("name", "style", "firstName", "lastName", "favBeer").ifNotExists().create()
        |schema.vertexLabel("beerStyle").properties("name", "style").ifNotExists().create()
        |schema.edgeLabel("likes").connection("beerDrinker", "beerStyle").ifNotExists().create()
        |schema.vertexLabel('beerDrinker').index('byStyle').materialized().by('style').ifNotExists().add()
        |schema.vertexLabel('beerDrinker').index('byFavBeer').materialized().by('favBeer').ifNotExists().add()
      """.stripMargin

    session.executeGraph(schema)
  }

}
