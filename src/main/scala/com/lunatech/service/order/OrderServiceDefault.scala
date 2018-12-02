package com.lunatech.service.order

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.service.order.persistence.OrderPersistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class OrderServiceDefault(val orderPersistence: OrderPersistence) extends OrderService {

  def getOrder(orderId: UUID): Future[Either[DatabaseError, OrderDto]] = {
    orderPersistence.getOrder(orderId).map {
      case Right(value) => Right(Order.orderToOrderDto(value))
      case Left(error) => Left(error)
    }
  }

  def createOrder: Future[Either[DatabaseError, OrderDto]] = ???

  def cancelOrder: Future[Either[DatabaseError, Boolean]] = ???

  def editOrder: Future[Either[DatabaseError, Order]] = ???

  def updateStatusOfOrder(): Future[Either[DatabaseError, Order]] = ???

  def listOfOrdersOfUser(userId: UUID): Future[Either[DatabaseError, List[Order]]] = ???

  def listOrders: Future[Either[DatabaseError, List[Order]]] = ???

}
