package com.lunatech.service.bookstore

import java.util.UUID

import scala.language.implicitConversions

case class Bookstore(id: Option[Int] = None, bookstoreId: UUID, name: String, location: String)

case class BookstoreDto(bookstoreId: UUID, name: String, location: String)

object BookstoreDto {

  implicit def bookstoreToBookstoreDto(bookstore: Bookstore): BookstoreDto = {
    BookstoreDto(bookstore.bookstoreId, bookstore.name, bookstore.location)
  }
}

case class BookstoreCreate(name: String, location: String)
