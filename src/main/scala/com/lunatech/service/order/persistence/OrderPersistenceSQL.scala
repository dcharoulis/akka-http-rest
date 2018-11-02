package com.lunatech.service.order.persistence

import com.lunatech.errors.ServiceError.AuthenticationError
import com.lunatech.external.OrderStatus
import com.lunatech.service.order.Order
import com.lunatech.utils.database.DBAccess


class OrderPersistenceSQL(val access: DBAccess) extends OrderPersistence {

  override def getOrder(orderId: Int): Either[AuthenticationError, Order] = ???

  override def createOrder: Either[AuthenticationError, Order] = ???

  override def cancelOrder: Either[AuthenticationError, Order] = ???

  override def editOrder: Either[AuthenticationError, Order] = ???

  override def updateStatusOfOrder(orderId: Int, orderStatus: OrderStatus): Either[AuthenticationError, Order] = ???

  override def listOfOrdersOfUser(userId: Int): Either[AuthenticationError, List[Order]] = ???

  override def listOrders: Either[AuthenticationError, List[Order]] = ???

}
