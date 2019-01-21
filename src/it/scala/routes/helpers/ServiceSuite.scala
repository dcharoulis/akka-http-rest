package routes.helpers

import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.lunatech.service.Routes
import com.lunatech.utils.config.Configuration
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Matchers, WordSpec}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.matching.Regex

abstract class ServiceSuite extends WordSpec with Matchers with ScalatestRouteTest
  with Routes with BeforeAndAfterAll with BeforeAndAfterEach {

  implicit val actorSystem: ActorSystem =
    ActorSystem("test-actor-system")

  //  implicit val executionContext: ExecutionContext = actorSystem.dispatcher
  //
  //  private val serviceHostname: (String, Int) =
  //    Option(System.getProperty("order-service_1_8080"))
  //      .map(unsafeExtractHostPort)
  //      .getOrElse(("localhost", 8080))
  //
  //  private val serviceConfig: Config =
  //    Configuration.endpointConfig("order", serviceHostname._1, serviceHostname._2)

  val configuration: Configuration = Configuration.default

  //  val dependencies: Dependencies = Dependencies.fromConfig(configuration)
  //
  //  val routes: Route = Routes.buildRoutes(dependencies)

  override def afterAll(): Unit =
    Await.result(actorSystem.terminate(), 10.seconds)

  private def unsafeExtractHostPort(string: String): (String, Int) = {
    val HostnameRegEx: Regex = "(.+):(\\d{4,5})".r
    string match {
      case HostnameRegEx(host, port) => (host, port.toInt)
      case _ => throw new Exception(s"Failed to parse hostname: $string")
    }
  }

}
