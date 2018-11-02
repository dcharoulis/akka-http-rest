package com.lunatech.service.book.persistence

import java.sql.Timestamp
import java.util.UUID

import com.lunatech.persistence.SlickJdbcProfile
import com.lunatech.service.book.Book
import slick.lifted.ProvenShape

trait BookTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class BookTable(tag: Tag) extends Table[Book](tag, "Books") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def bookId: Rep[UUID] = column[UUID]("book_id")

    def title: Rep[String] = column[String]("title")

    def timestamp: Rep[Timestamp] = column[Timestamp]("timestamp")

    def genre: Rep[String] = column[String]("genre")

    def price: Rep[Double] = column[Double]("price")

    def quantity: Rep[Int] = column[Int]("quantity")

    def * : ProvenShape[Book] = (
      id.?,
      bookId,
      title,
      timestamp,
      genre,
      price,
      quantity
    ) <> ((Book.apply _).tupled, Book.unapply)

  }

}
