package feeds.examples.solr

import java.text.SimpleDateFormat

import com.datastax.gatling.stress.core.BaseFeed
import com.datastax.gatling.stress.helpers.SolrQueryBuilder
import com.typesafe.scalalogging.LazyLogging
import jodd.util.BCrypt
import org.joda.time.DateTime

class AccountFeed extends BaseFeed with LazyLogging {

  def writeAccount = Iterator.continually(getAccountRowData)

  val sdf = new SimpleDateFormat("yyyy-MM-dd")

  private val localeQ = new SolrQueryBuilder().withFilterQuery("locale:en-us").build

  private def getAccountRowData = {
    Map(
      "account_id" -> getUuid,
      "first_name" -> faker.name.firstName,
      "last_name" -> faker.name.lastName,
      "email" -> faker.internet.emailAddress,
      "country_code" -> faker.address.countryCode,
      "locale" -> getRandom(Seq("en-us", "en-gb", "en-ca", "fr-fr", "it-it")),
      "pass" -> BCrypt.hashpw(faker.pokemon.name, BCrypt.gensalt()),
      "age" -> faker.number.numberBetween(10, 65),
      "birthday" -> sdf.format(faker.date.between(DateTime.parse("1965-01-01").toDate, DateTime.parse("2012-01-01").toDate)),
      "created_date" -> getCurrentTimestamp,
      "updated_date" -> getCurrentTimestamp
    )
  }

  def solrLocaleEnUs = Iterator.continually(getSolrData)

  private def getSolrData = {
    Map("solr_query" -> localeQ)
  }
}