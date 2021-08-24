package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class RampUsersLoadSimulation extends Simulation {

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
  val scn: ScenarioBuilder = scenario("Eight Scenario")
    .exec(getAllComments)
    .pause(5)
    .exec(getAllPosts)
    .pause(5)
    .exec(getAllComments)

  //4. Load Scenario
  setUp(
    scn.inject(
      nothingFor(5 seconds),
      constantUsersPerSec(10) during (10 seconds),
      rampUsersPerSec(1) to (5) during (20 seconds))
  ).protocols(httpProtocol)
}
