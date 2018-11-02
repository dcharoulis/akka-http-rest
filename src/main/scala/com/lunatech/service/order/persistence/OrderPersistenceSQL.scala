package com.lunatech.service.order.persistence

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.external.OrderStatus
import com.lunatech.service.order.Order
import com.lunatech.utils.database.DBAccess

import scala.concurrent.Future


class OrderPersistenceSQL(val access: DBAccess) extends OrderPersistence {

  override def getOrder(orderId: UUID): Future[Either[DatabaseError, Order]] = ???

  override def createOrder: Future[Either[DatabaseError, Order]] = ???

  override def cancelOrder: Future[Either[DatabaseError, Order]] = ???

  override def editOrder: Future[Either[DatabaseError, Order]] = ???

  override def updateStatusOfOrder(orderId: Int, orderStatus: OrderStatus): Future[Either[DatabaseError, Order]] = ???

  override def listOfOrdersOfUser(userId: Int): Future[Either[DatabaseError, List[Order]]] = ???

  override def listOrders: Future[Either[DatabaseError, List[Order]]] = ???
}
