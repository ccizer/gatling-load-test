package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class BasicSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Scenario Definition
  val scn: ScenarioBuilder = scenario("First Scenario")
    .exec(http("Get All Posts")
      .get("/posts"))

  //3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
