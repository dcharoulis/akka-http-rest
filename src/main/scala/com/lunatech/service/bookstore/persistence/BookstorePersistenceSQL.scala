package com.lunatech.service.bookstore.persistence

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.{GenericDatabaseError, RecordNotFound}
import com.lunatech.service.bookstore.{Bookstore, BookstoreCreate}
import com.lunatech.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BookstorePersistenceSQL(val dbAccess: DBAccess) extends BookstorePersistence {

  import dbAccess._
  import dbAccess.profile.api._

  override def getBookstores: Future[Either[DatabaseError, Seq[Bookstore]]] = {
    db.run(Bookstores.result).
      transformWith {
        case Success(bookstores) => Future.successful(Right(bookstores))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def getBookstore(bookstoreId: UUID): Future[Either[DatabaseError, Bookstore]] = {
    db.run(Bookstores.filter(_.bookstoreId === bookstoreId).result.headOption).
      transformWith {
        case Success(optBookstore) => optBookstore match {
          case Some(bookstore) => Future.successful(Right(bookstore))
          case None => Future.successful(Left(RecordNotFound))
        }
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def createBookstore(data: BookstoreCreate): Future[Either[DatabaseError, Bookstore]] = {
    val bookstoreRow = Bookstore(
      bookstoreId = UUID.randomUUID(), name = data.name, location = data.location
    )
    val bookstoreAction = (Bookstores returning Bookstores.map(_.id) into (
      (book, newId) => book.copy(id = Some(newId)))
      ) += bookstoreRow
    db.run(bookstoreAction).
      transformWith {
        case Success(bookstore) => Future.successful(Right(bookstore))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  def deleteAllBooks: Future[Either[DatabaseError, Boolean]] = {
    db.run(Books.delete).transformWith {
      case Success(res) => res match {
        case 0 => Future.successful(Right(false))
        case 1 => Future.successful(Right(true))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

}
