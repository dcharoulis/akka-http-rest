package com.lunatech.service.book.persistence

import java.sql.Timestamp
import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.service.book._

import scala.concurrent.Future


trait BookPersistence {

  def getBook(bookId: UUID): Future[Either[DatabaseError, Book]]

  def createBook(data: BookCreate): Future[Either[DatabaseError, Book]]

  def editBook(bookId: UUID, data: Book): Future[Either[DatabaseError, Book]]

  def listBooks(title: Option[String],
                createdBefore: Option[Timestamp],
                genre: Option[BookGenreEndpointView]): Future[Either[DatabaseError, Seq[Book]]]

  def getBookstoreBooks(bookstoreId: UUID): Future[Either[DatabaseError, Seq[Book]]]

  def addBookToBookstore(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]]

  def updateBookstoreBook(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]]

  def deleteAllBooks: Future[Either[DatabaseError, Boolean]]
}
