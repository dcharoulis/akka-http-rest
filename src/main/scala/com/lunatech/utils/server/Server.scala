package com.lunatech.utils.server

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext

trait Server {
  implicit val system: ActorSystem = ActorSystem("AkkaHttpWorkshop")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher
}
