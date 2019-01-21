package com.lunatech.service.order

import java.util.UUID

import com.lunatech.errors.DatabaseError

import scala.concurrent.Future


trait OrderService {

  def getOrder(orderId: UUID): Future[Either[DatabaseError, OrderDto]]

  def createOrder: Future[Either[DatabaseError, OrderDto]]

  def cancelOrder: Future[Either[DatabaseError, Boolean]]

  def editOrder: Future[Either[DatabaseError, Order]]

  def updateStatusOfOrder(): Future[Either[DatabaseError, Order]]

  def listOfOrdersOfUser(userId: UUID): Future[Either[DatabaseError, List[Order]]]

  def listOrders: Future[Either[DatabaseError, List[Order]]]

}
