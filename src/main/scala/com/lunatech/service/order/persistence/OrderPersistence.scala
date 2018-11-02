package com.lunatech.service.order.persistence

import com.lunatech.errors.ServiceError.AuthenticationError
import com.lunatech.external.OrderStatus
import com.lunatech.service.order.Order

trait OrderPersistence {

  def getOrder(orderId: Int): Either[AuthenticationError, Order]

  def createOrder: Either[AuthenticationError, Order]

  def cancelOrder: Either[AuthenticationError, Order]

  def editOrder: Either[AuthenticationError, Order]

  def updateStatusOfOrder(orderId: Int, orderStatus: OrderStatus): Either[AuthenticationError, Order]

  def listOfOrdersOfUser(userId: Int): Either[AuthenticationError, List[Order]]

  def listOrders: Either[AuthenticationError, List[Order]]
}
