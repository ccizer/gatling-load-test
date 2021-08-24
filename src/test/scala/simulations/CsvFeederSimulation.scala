package simulations

import io.gatling.core.Predef._
import io.gatling.core.feeder.BatchableFeederBuilder
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

class CsvFeederSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Import CSV file
  val csvFeeder: BatchableFeederBuilder[String]#F = csv("data/postCsvFile.csv").circular

  //3. Call Definition
  def getSpecificPostWithCsvFeeder: ChainBuilder = {
    repeat(10) {
      feed(csvFeeder)
        .exec(
          http("Get Specific post")
            .get("/posts/${postId}")
            .check(jsonPath("$.title").is("${postTitle}"))
            .check(status.in(200 to 304))
        )
    }
  }

  //4. Scenario Definition
  val scn: ScenarioBuilder = scenario("Fifth Scenario")
    .exec(getSpecificPostWithCsvFeeder)

  //5. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
