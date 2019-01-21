package com.lunatech.service.order

import java.util.UUID

case class Order(
                  id: Option[Int],
                  orderId: UUID,
                  userId: Int,
                  totalPrice: Double,
                  status: String,
                  deliveryAddress: String,
                  comments: String
                )

object Order {
  def orderToOrderDto(order: Order): OrderDto = {
    OrderDto(orderId = order.orderId, totalPrice = order.totalPrice,
      status = order.status, deliveryAddress = order.deliveryAddress, comments = order.comments
    )
  }
}

case class OrderDto(orderId: UUID,
                    totalPrice: Double,
                    status: String,
                    deliveryAddress: String,
                    comments: String) {

}


case class OrderCreate(userId: UUID,
                       totalPrice: Double,
                       status: String,
                       deliveryAddress: String,
                       comments: String)




