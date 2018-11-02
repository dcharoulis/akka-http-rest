package com.lunatech.service.order

import com.lunatech.service.order.persistence.OrderPersistence


class OrderServiceDefault(val orderPersistence: OrderPersistence) extends OrderService {

  def getOrder(orderId: Int): Either[Error, Order] = ???

  def createOrder: Either[Error, Order] = ???

  def cancelOrder: Either[Error, Order] = ???

  def editOrder: Either[Error, Order] = ???

  def updateStatusOfOrder(): Either[Error, Order] = ???

  def listOfOrdersOfUser(userId: Int): Either[Error, List[Order]] = ???

  def listOrders: Either[Error, List[Order]] = ???

}
