package simulations

import io.gatling.core.Predef._
import io.gatling.core.structure.{ChainBuilder, ScenarioBuilder}
import io.gatling.http.Predef._
import io.gatling.http.protocol.HttpProtocolBuilder

import scala.concurrent.duration._
import scala.language.postfixOps

class RuntimeCommandParametersSimulation extends Simulation {

  //1. Helper method to get the property or the default value
  private def getProperty(propertyName: String, defaultValue: String): String = {
    Option(System.getenv(propertyName))
      .orElse(Option(System.getProperty(propertyName)))
      .getOrElse(defaultValue)
  }

  //2. Assign the properties to the variables
  def userCount: Int = getProperty("USERS", "1").toInt

  def rampDuration: Int = getProperty("RAMP_DURATION", "10").toInt

  def testDuration: Int = getProperty("DURATION", "20").toInt

  //3. Print variables
  before {
    println(s"Running test with ${userCount} users")
    println(s"Ramping users over ${rampDuration} seconds")
    println(s"Total test duration: ${testDuration} seconds")
  }

  //4. HTTP Configuration
  val httpProtocol: HttpProtocolBuilder = http
    .baseUrl("https://jsonplaceholder.typicode.com")
    .header("Accept", "application/json")

  //5. Call Definitions
  def getAllPosts: ChainBuilder = {
    exec(
      http("Get All Post")
        .get("/posts")
        .check(status.in(200 to 304))
    )
  }

  //6. Scenario Definition
  val scn: ScenarioBuilder = scenario("Eleventh Scenario")
    .forever() {
      exec(getAllPosts)
    }


  //7. Load Scenario
  setUp(
    scn.inject(
      nothingFor(5 seconds),
      rampUsers(userCount) during (rampDuration seconds)))
    .protocols(httpProtocol)
    .maxDuration(testDuration seconds)
}
