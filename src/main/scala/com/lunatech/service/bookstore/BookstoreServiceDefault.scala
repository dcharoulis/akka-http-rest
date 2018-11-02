package com.lunatech.service.bookstore

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.GenericDatabaseError
import com.lunatech.service.book.persistence.BookPersistence
import com.lunatech.service.book.{BookDto, BookstoreBookDto}
import com.lunatech.service.bookstore.persistence.BookstorePersistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookstoreServiceDefault(val bookstorePersistence: BookstorePersistence, val bookPersistence: BookPersistence) extends BookstoreService {

  def getBookstores: Future[Either[DatabaseError, List[BookstoreDto]]] = {
    bookstorePersistence.getBookstores.map {
      case Right(value) => Right(value.map(bookstore => BookstoreDto.bookstoreToBookstoreDto(bookstore)).toList)
      case Left(_) => Left(GenericDatabaseError)
    }
  }

  override def getBookstore(bookstoreId: UUID): Future[Either[DatabaseError, BookstoreDto]] = {
    bookstorePersistence.getBookstore(bookstoreId).map {
      case Right(bookstore) => Right(BookstoreDto.bookstoreToBookstoreDto(bookstore))
      case Left(error) => Left(error)
    }
  }

  def createBookstore(bookstoreCreate: BookstoreCreate): Future[Either[DatabaseError, BookstoreDto]] = {
    bookstorePersistence.createBookstore(bookstoreCreate).map {
      case Right(value) => Right(BookstoreDto.bookstoreToBookstoreDto(value))
      case Left(_) => Left(GenericDatabaseError)
    }
  }

  override def getBookstoreBooks(bookstoreId: UUID): Future[Either[DatabaseError, List[BookDto]]] = {
    bookPersistence.getBookstoreBooks(bookstoreId).map {
      case Right(value) => Right(value.map(book => BookDto.bookToBookDto(book)).toList)
      case Left(error) => Left(error)
    }
  }

  override def addBookToBookstore(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]] = {
    bookPersistence.addBookToBookstore(bookstoreId, bookstoreBookDto).map {
      case Right(book) => Right(book)
      case Left(error) => Left(error)
    }
  }

  override def updateBookstoreBook(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]] = {
    bookPersistence.updateBookstoreBook(bookstoreId, bookstoreBookDto).map {
      case Right(book) => Right(book)
      case Left(error) => Left(error)
    }
  }
}
