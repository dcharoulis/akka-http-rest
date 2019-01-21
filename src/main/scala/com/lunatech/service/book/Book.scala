package com.lunatech.service.book

import java.sql.Timestamp
import java.util.UUID

import scala.language.{higherKinds, implicitConversions}

case class Book(id: Option[Int] = None, bookId: UUID, title: String, timestamp: Timestamp, genre: String, price: Double, quantity: Int)

object Book {

  implicit def bookRowToBook(book: Book, quantity: Int): Book = {
    Book(book.id, book.bookId, book.title, book.timestamp, book.genre, book.price, book.quantity)
  }

}

case class BookDto(bookId: UUID, title: String, createdAt: Timestamp, genre: String, price: Double, quantity: Int)

case class BookDtoCompact(bookId: UUID, title: String, genre: String)

object BookDto {

  implicit def bookToBookDto(book: Book): BookDto = {
    BookDto(book.bookId, book.title, book.timestamp, book.genre, book.price, book.quantity)
  }

  implicit def bookToBookDtoCompact(book: Book): BookDtoCompact = {
    BookDtoCompact(book.bookId, book.title, book.genre)
  }

}

case class BookCreate(title: String, genre: String, price: Double, quantity: Int)

case class BookstoreBookDto(bookstoreId: UUID, bookId: UUID, quantity: Int)