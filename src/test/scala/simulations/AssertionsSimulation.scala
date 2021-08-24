package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class AssertionsSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Call Definitions
  def getAllComments: ChainBuilder = {
    exec(
      http("Get All Comments")
        .get("/comments")
        .check(status.in(200 to 304))
    )
  }

  def getAllPosts: ChainBuilder = {
    exec(
      http("Get All Post")
        .get("/posts")
        .check(status.in(200 to 304))
    )
  }

  //3. Scenario Definition
  val scn: ScenarioBuilder = scenario("Tenth Scenario")
    .exec(getAllPosts)
    .pause(5)
    .exec(getAllComments)

  //4. Load Scenario
  setUp(
    scn.inject(
      nothingFor(5 seconds),
      atOnceUsers(10),
      rampUsers(5) during (5 seconds)))
    .protocols(httpProtocol)
    .assertions(
      global.responseTime.max.lt(500),
      global.successfulRequests.percent.gt(95)
    )
}
