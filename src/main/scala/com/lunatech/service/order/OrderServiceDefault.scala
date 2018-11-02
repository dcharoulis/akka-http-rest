package com.lunatech.service.order

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.service.order.persistence.OrderPersistence

import scala.concurrent.Future


class OrderServiceDefault(val orderPersistence: OrderPersistence) extends OrderService {

  def getOrder(orderId: UUID): Future[Either[DatabaseError, Order]] = ???

  def createOrder: Future[Either[DatabaseError, Order]] = ???

  def cancelOrder: Future[Either[DatabaseError, Order]] = ???

  def editOrder: Future[Either[DatabaseError, Order]] = ???

  def updateStatusOfOrder(): Future[Either[DatabaseError, Order]] = ???

  def listOfOrdersOfUser(userId: Int): Future[Either[DatabaseError, List[Order]]] = ???

  def listOrders: Future[Either[DatabaseError, List[Order]]] = ???

}
