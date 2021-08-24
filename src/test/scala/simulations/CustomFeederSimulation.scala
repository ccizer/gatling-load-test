package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.util.Random

class CustomFeederSimulation extends Simulation {

  //1. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //2. Helper variables and methods
  var idNumbers: Iterator[Int] = (11 to 20).iterator
  val rnd = new Random()

  def randomString(length: Int): String = {
    rnd.alphanumeric.filter(_.isLetter).take(length).mkString
  }

  //3. Custom feeder mapper
  val customFeeder: Iterator[Map[String, Any]] = Iterator.continually(Map(
    "userId" -> idNumbers.next(),
    "title" -> ("Title-" + randomString(5)),
    "body" -> ("Body-" + randomString(6)),
  ))

  //4. Call Definition
  def createPost: ChainBuilder = {
    repeat(5) {
      feed(customFeeder)
        .exec(http("Create New Post")
          .post("/posts")
          .body(ElFileBody("bodies/postNewPost.json")).asJson
          .check(status.is(201)))
        .pause(1)
    }
  }

  //5. Scenario Definition
  val scn: ScenarioBuilder = scenario("Sixth Scenario")
    .exec(createPost)

  //6. Load Scenario
  setUp(
    scn.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
