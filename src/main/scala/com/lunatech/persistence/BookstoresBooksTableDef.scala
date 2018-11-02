package com.lunatech.persistence

import java.util.UUID

import slick.lifted.{PrimaryKey, ProvenShape}

trait BookstoresBooksTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  case class BookstoreBook(bookstoreId: UUID, bookId: UUID, quantity: Int)

  class BookstoresBooksTable(tag: Tag)
    extends Table[BookstoreBook](tag, "BookstoreToBook") {

    def bookstoreId: Rep[UUID] = column[UUID]("bookstoreId")

    def bookId: Rep[UUID] = column[UUID]("bookId")

    def quantity: Rep[Int] = column[Int]("quantity")

    //    def bookstoreFK =
    //      foreignKey("bookstore_fk", bookstoreId, TableQuery[Bookstore])(bookstore =>
    //        bookstore.id) //, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)
    //
    //    def bookFK =
    //      foreignKey("book_fk", bookId, TableQuery[Book])(book =>
    //        book.id) //, onUpdate = ForeignKeyAction.NoAction, onDelete = ForeignKeyAction.NoAction)

    def primaryKey: PrimaryKey = primaryKey("pk", (bookstoreId, bookId))

    def * : ProvenShape[BookstoreBook] = (bookstoreId, bookId, quantity) <> (BookstoreBook.tupled, BookstoreBook.unapply)
  }

}