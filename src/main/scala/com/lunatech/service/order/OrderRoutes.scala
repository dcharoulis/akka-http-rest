package com.lunatech.service.order

import akka.http.scaladsl.server.Route
import com.lunatech.service.Routes

class OrderRoutes(val orderService: OrderService) extends Routes {
  val orderRoutes: Route = ???
}

object OrderRoutes {

}