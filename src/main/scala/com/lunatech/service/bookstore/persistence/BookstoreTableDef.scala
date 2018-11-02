package com.lunatech.service.bookstore.persistence

import java.util.UUID

import com.lunatech.persistence.SlickJdbcProfile
import com.lunatech.service.bookstore.Bookstore
import slick.lifted.ProvenShape

trait BookstoreTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class BookstoreTable(tag: Tag) extends Table[Bookstore](tag, "Bookstores") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def bookstoreId: Rep[UUID] = column[UUID]("book_id")

    def title: Rep[String] = column[String]("title")

    def location: Rep[String] = column[String]("location")

    def * : ProvenShape[Bookstore] = (
      id.?,
      bookstoreId,
      title,
      location,
    ) <> (Bookstore.tupled, Bookstore.unapply)
  }

}

