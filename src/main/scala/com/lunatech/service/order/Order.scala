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
