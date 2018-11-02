package com.lunatech.service.book

import java.sql.Timestamp
import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.GenericDatabaseError
import com.lunatech.service.book.persistence.BookPersistence

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookServiceDefault(val bookPersistence: BookPersistence) extends BookService {

  override def getBook(bookId: UUID): Future[Either[DatabaseError, BookDto]] = {
    bookPersistence.getBook(bookId).map {
      case Right(book) => Right(BookDto.bookToBookDto(book))
      case Left(error) => Left(error)
    }
  }

  override def createBook(data: BookCreate): Future[Either[DatabaseError, BookDto]] = {
    bookPersistence.createBook(data).map {
      case Right(value) => Right(BookDto.bookToBookDto(value))
      case Left(_) => Left(GenericDatabaseError)
    }
  }

  override def editBook(bookId: UUID, data: Book): Future[Either[DatabaseError, BookDto]] = ???


  def getBooksNormal(
                      title: Option[String],
                      createdBefore: Option[Timestamp],
                      genre: Option[BookGenreEndpointView]
                    ): Future[Either[DatabaseError, List[BookDto]]] =
    bookPersistence.listBooks(title, createdBefore, genre).map {
      case Right(value) => Right(value.map(book => BookDto.bookToBookDto(book)).toList)
      case Left(error) => Left(error)
    }

  def getBooksCompact(
                       title: Option[String],
                       createdBefore: Option[Timestamp],
                       genre: Option[BookGenreEndpointView]
                     ): Future[Either[DatabaseError, List[BookDtoCompact]]] =
    bookPersistence.listBooks(title, createdBefore, genre).map {
      case Right(value) => Right(value.map(book => BookDto.bookToBookDtoCompact(book)).toList)
      case Left(error) => Left(error)
    }


}
