package com.lunatech.external

sealed abstract class OrderStatus(
                                   val status: String
                                 )

object OrderStatus {

  case object ORDER_CREATED extends OrderStatus(status = "ORDER_CREATED")

  case object ORDER_PENDING extends OrderStatus(status = "ORDER_PENDING")

  case object ORDER_COMPLETED extends OrderStatus(status = "ORDER_COMPLETED")

  case object ORDER_CANCELED extends OrderStatus(status = "ORDER_CANCELED")

}
