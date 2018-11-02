package com.lunatech.service.bookstore

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.service.book.{BookDto, BookstoreBookDto}

import scala.concurrent.Future

trait BookstoreService {

  def getBookstores: Future[Either[DatabaseError, List[BookstoreDto]]]

  def getBookstore(bookstoreId: UUID): Future[Either[DatabaseError, BookstoreDto]]

  def createBookstore(bookstoreCreate: BookstoreCreate): Future[Either[DatabaseError, BookstoreDto]]

  def getBookstoreBooks(bookstoreId: UUID): Future[Either[DatabaseError, List[BookDto]]]

  def addBookToBookstore(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]]

  def updateBookstoreBook(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]]

}
