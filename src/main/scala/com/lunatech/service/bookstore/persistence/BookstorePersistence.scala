package com.lunatech.service.bookstore.persistence

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.service.bookstore.{Bookstore, BookstoreCreate}

import scala.concurrent.Future

trait BookstorePersistence {

  def getBookstore(bookstoreId: UUID): Future[Either[DatabaseError, Bookstore]]

  def createBookstore(data: BookstoreCreate): Future[Either[DatabaseError, Bookstore]]

  def getBookstores: Future[Either[DatabaseError, Seq[Bookstore]]]

  def deleteAllBooks: Future[Either[DatabaseError, Boolean]]

}
