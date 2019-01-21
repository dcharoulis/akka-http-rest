package com.lunatech

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.lunatech.service.{Dependencies, Routes}
import com.lunatech.utils.config.Configuration
import com.lunatech.utils.server.Server

import scala.concurrent.Future
import scala.util.{Failure, Success}

object Main extends App with Server {

  def startApplication(): Unit = {
    val configuration: Configuration = Configuration.default

    val dependencies: Dependencies = Dependencies.fromConfig(configuration)

    val routes: Route = Routes.buildRoutes(dependencies)

    val serverBinding: Future[Http.ServerBinding] = Http().bindAndHandle(routes, configuration.serverConfig.host, configuration.serverConfig.port)

    serverBinding.onComplete {
      case Success(bound) =>
        println(s"com.lunatech.utils.server.Server online at http://${bound.localAddress.getHostString}:${bound.localAddress.getPort}/")
      case Failure(e) =>
        Console.err.println(s"com.lunatech.utils.server.Server could not start!")
        e.printStackTrace()
        system.terminate()
    }
  }

  startApplication()

}
