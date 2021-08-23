package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class CodeReuseSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Call Definitions
  def getAllComments: ChainBuilder = {
    repeat(3) {
      exec(
        http("Get All Comments")
          .get("/comments")
          .check(status.in(200 to 304))
      )
    }
  }

  def getAllPosts: ChainBuilder = {
    repeat(5) {
      exec(
        http("Get All Post")
          .get("/posts")
          .check(status.in(200 to 304))
      )
    }
  }

  //3. Scenario Definition
  val scn: ScenarioBuilder = scenario("Fourth Scenario")
    .exec(getAllComments)
    .pause(5)
    .exec(getAllPosts)

  //4. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
