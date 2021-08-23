package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration.DurationInt

class PauseAndCheckStatusSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Scenario Definition
  val scn: ScenarioBuilder = scenario("Second Scenario")
    .exec(http("Get All Posts")
      .get("/posts")
      .check(status.is(200)))
    .pause(5)

    .exec(http("Get Specific Post")
      .get("/posts/1")
      .check(status.in(200 to 210)))
    .pause(1, 10)

    .exec(http("Get All Posts - 2nd call")
      .get("/posts")
      .check(status.not(404), status.not(500)))
    .pause(2000.milliseconds)

  //3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
