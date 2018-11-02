package com.lunatech.external

sealed abstract class BookStatus(
                                   val status: String
                                 )

object BookStatus {

  case object OnStock extends BookStatus(status = "OnStock")

  case object ORDER_PENDING extends BookStatus(status = "ORDER_PENDING")

  case object ORDER_COMPLETED extends BookStatus(status = "ORDER_COMPLETED")

  case object ORDER_CANCELED extends BookStatus(status = "ORDER_CANCELED")

}
