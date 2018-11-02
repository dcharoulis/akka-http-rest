package com.lunatech.service.order.persistence

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.external.OrderStatus
import com.lunatech.service.order.Order

import scala.concurrent.Future

trait OrderPersistence {

  def getOrder(orderId: UUID): Future[Either[DatabaseError, Order]]

  def createOrder: Future[Either[DatabaseError, Order]]

  def cancelOrder: Future[Either[DatabaseError, Order]]

  def editOrder: Future[Either[DatabaseError, Order]]

  def updateStatusOfOrder(orderId: Int, orderStatus: OrderStatus): Future[Either[DatabaseError, Order]]

  def listOfOrdersOfUser(userId: Int): Future[Either[DatabaseError, List[Order]]]

  def listOrders: Future[Either[DatabaseError, List[Order]]]
}
