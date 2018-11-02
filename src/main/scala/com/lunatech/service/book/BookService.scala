package com.lunatech.service.book

import java.sql.Timestamp
import java.util.UUID

import com.lunatech.errors.DatabaseError

import scala.concurrent.Future

trait BookService {

  def getBook(bookId: UUID): Future[Either[DatabaseError, BookDto]]

  def createBook(data: BookCreate): Future[Either[DatabaseError, BookDto]]

  def editBook(bookId: UUID, data: Book): Future[Either[DatabaseError, BookDto]]

  def getBooksNormal(
                      title: Option[String],
                      createdBefore: Option[Timestamp],
                      genre: Option[BookGenreEndpointView]
                    ): Future[Either[DatabaseError, List[BookDto]]]

  def getBooksCompact(
                       title: Option[String],
                       createdBefore: Option[Timestamp],
                       genre: Option[BookGenreEndpointView]
                     ): Future[Either[DatabaseError, List[BookDtoCompact]]]

}
