package com.lunatech.persistence

import com.lunatech.service.book.persistence.BookTableDef
import com.lunatech.service.bookstore.persistence.BookstoreTableDef
import com.lunatech.service.order.persistence.OrderTableDef
import com.lunatech.service.user.persistence.UserTableDef

trait Schema extends SlickJdbcProfile
  with OrderTableDef
  with BookTableDef
  with BookstoreTableDef
  with BookstoresBooksTableDef
  with UserTableDef {

  import profile.api._

  implicit lazy val Users: TableQuery[UserTable] = TableQuery[UserTable]
  implicit lazy val Bookstores: TableQuery[BookstoreTable] = TableQuery[BookstoreTable]
  implicit lazy val BookstoresBooks: TableQuery[BookstoresBooksTable] = TableQuery[BookstoresBooksTable]
  implicit lazy val Orders: TableQuery[OrderTable] = TableQuery[OrderTable]
  implicit lazy val Books: TableQuery[BookTable] = TableQuery[BookTable]

  lazy val Schema: profile.DDL =
    Users.schema ++ Orders.schema ++ Books.schema ++ Bookstores.schema ++ BookstoresBooks.schema

}
