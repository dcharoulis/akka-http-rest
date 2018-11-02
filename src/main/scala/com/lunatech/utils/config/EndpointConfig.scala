package com.lunatech.utils.config

import com.lunatech.utils.config.EndpointConfig.{HTTP, Protocol, TLS}
import com.typesafe.config.Config

object EndpointConfig {

  sealed trait Protocol

  case object HTTP extends Protocol

  case object TLS extends Protocol

}

class EndpointConfig(config: Config, endpointConfigPath: String) {

  lazy val sslConfig: Config = {
    val endpointSslConfig = config.getConfig(s"$endpointConfigPath.ssl-config")
    endpointSslConfig withFallback config.getConfig("akka.ssl-config") withFallback config.getConfig("ssl-config")
  }
  val hostname: String = config.getString(s"$endpointConfigPath.hostname")
  val port: Option[Int] = Option(config.getInt(s"$endpointConfigPath.port"))
  val protocol: Protocol =
    if (config.hasPath(s"$endpointConfigPath.tls") && config.getBoolean(s"$endpointConfigPath.tls.enabled")) TLS
    else HTTP
  val mutualTlsEnabled: Boolean = config.hasPath(s"$endpointConfigPath.tls.mutual") && config.getBoolean(
    s"$endpointConfigPath.tls.mutual.enabled"
  )

  def value(key: String): String = config.getString(s"$endpointConfigPath.$key")
}
