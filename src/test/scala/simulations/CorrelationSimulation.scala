package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class CorrelationSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Scenario Definition
  val scn: ScenarioBuilder = scenario("Third Scenario")

    // First, Check the title of the first post
    .exec(http("Get Specific Post")
      .get("/posts/1")
      .check(jsonPath("$.title").is("sunt aut facere repellat provident occaecati excepturi optio reprehenderit")))

    // Second, Get the id of the second element in the posts list and save it
    .exec(http("Get All Post")
      .get("/posts")
      .check(jsonPath("$[1].id").saveAs("postId")))

    // Third, Check the title of the saved post id
    .exec(http("Get Specific Post")
      .get("/posts/${postId}")
      .check(jsonPath("$.title").is("qui est esse")))

  //3. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
