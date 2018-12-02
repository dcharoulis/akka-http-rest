package com.lunatech.service.order.persistence

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.{GenericDatabaseError, RecordNotFound}
import com.lunatech.external.OrderStatus
import com.lunatech.service.order.Order
import com.lunatech.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


class OrderPersistenceSQL(val dbAccess: DBAccess) extends OrderPersistence {

  import dbAccess._
  import dbAccess.profile.api._

  override def getOrder(orderId: UUID): Future[Either[DatabaseError, Order]] = {
    db.run(
      Orders.filter(_.orderId === orderId).result.headOption
    ).transformWith {
      case Success(optOrder) => optOrder match {
        case Some(order) => Future.successful(Right(order))
        case None => Future.successful(Left(RecordNotFound))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  override def createOrder: Future[Either[DatabaseError, Order]] = ???

  override def cancelOrder: Future[Either[DatabaseError, Order]] = ???

  override def editOrder: Future[Either[DatabaseError, Order]] = ???

  override def updateStatusOfOrder(orderId: Int, orderStatus: OrderStatus): Future[Either[DatabaseError, Order]] = ???

  override def listOfOrdersOfUser(userId: Int): Future[Either[DatabaseError, List[Order]]] = ???

  override def listOrders: Future[Either[DatabaseError, List[Order]]] = ???
}
