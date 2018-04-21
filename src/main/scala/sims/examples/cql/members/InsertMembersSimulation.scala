package sims.examples.cql.members

import actions.examples.cql.MemberActions
import com.datastax.gatling.plugin.CqlPredef._
import com.datastax.gatling.stress.core.BaseSimulation
import com.datastax.gatling.stress.libs.SimConfig
import feeds.examples.cql.MemberFeed
import io.gatling.core.Predef._

class InsertMembersSimulation extends BaseSimulation {

  val simName = "examples"
  val scenarioName = "insertMembers"

  val simConf = new SimConfig(conf, simName, scenarioName)

  val actions = new MemberActions(cass, simConf)

  val writeFeed = new MemberFeed

  val writeScenario = scenario("Insert")
      .feed(writeFeed.getMember)
      .exec(actions.writeMember)
      .feed(writeFeed.getMemberAddress)
      .exec(actions.writeMemberAddresses)
      .feed(writeFeed.getMemberAddress)
      .exec(actions.writeMemberAddresses)
      .feed(writeFeed.getMemberAddress)
      .exec(actions.writeMemberAddresses)

  setUp(

    loadGenerator.rampUpToConstant(writeScenario, simConf)

  ).protocols(cqlProtocol)
}