package com.lunatech.utils.config

import com.lunatech.utils.server.ServerConfig
import com.typesafe.config.{Config, ConfigFactory}

import scala.concurrent.duration.{FiniteDuration, _}


case class Configuration(
                          initializationTimeoutConfiguration: FiniteDuration,
                          serverConfig: ServerConfig
                        )

object Configuration extends ConfigurationFunctions {

  sealed trait ClientConfigType[+Mocks]

  case class IsHttp(endpointConfig: EndpointConfig) extends ClientConfigType[Nothing]

  case class IsMocked[Mocks](mocks: Mocks) extends ClientConfigType[Mocks]

  case class ClientConfig(name: String, host: String, port: Int)

}

trait ConfigurationFunctions {

  def default: Configuration = {
    val config = ConfigFactory.load()

    new Configuration(
      FiniteDuration.apply(config.getDuration("server.initialization-timeout").toMillis, MILLISECONDS),
      ServerConfig(config)
    )
  }

  def endpointConfig(name: String, host: String, port: Int): Config =
    ConfigFactory.parseString(
      s"""$name.endpoint {
         |  mocked = false
         |  hostname = $host
         |  base-path = ""
         |  port = $port
         |  tls {
         |    enabled = false
         |  }
         |  ssl-config {}
         |}
      """.stripMargin)

}
