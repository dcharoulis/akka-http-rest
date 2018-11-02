package com.lunatech.service

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.lunatech.service.auth.{AuthService, AuthServiceDefault}
import com.lunatech.service.book.persistence.BookPersistenceSQL
import com.lunatech.service.book.{BookService, BookServiceDefault}
import com.lunatech.service.bookstore.persistence.BookstorePersistenceSQL
import com.lunatech.service.bookstore.{BookstoreService, BookstoreServiceDefault}
import com.lunatech.service.order.persistence.OrderPersistenceSQL
import com.lunatech.service.order.{OrderService, OrderServiceDefault}
import com.lunatech.service.user.persistence.UserPersistenceSQL
import com.lunatech.service.user.{UserService, UserServiceDefault}
import com.lunatech.utils.config.Configuration
import com.lunatech.utils.database.DBAccess

case class Dependencies(
                         authService: AuthService,
                         userService: UserService,
                         orderService: OrderService,
                         bookService: BookService,
                         bookstoreService: BookstoreService
                       )

object Dependencies {

  def fromConfig(configuration: Configuration)(implicit system: ActorSystem, mat: ActorMaterializer): Dependencies = {

    val dbAccess = DBAccess(system)

    val userPersistence = new UserPersistenceSQL(dbAccess)
    val orderPersistence = new OrderPersistenceSQL(dbAccess)
    val bookstorePersistence = new BookstorePersistenceSQL(dbAccess)
    val bookPersistence = new BookPersistenceSQL(dbAccess, bookstorePersistence)

    val authService = new AuthServiceDefault(userPersistence)
    val userService = new UserServiceDefault(userPersistence)
    val orderService = new OrderServiceDefault(orderPersistence)
    val bookService = new BookServiceDefault(bookPersistence)
    val bookstoreService = new BookstoreServiceDefault(bookstorePersistence, bookPersistence)

    Dependencies(authService, userService, orderService, bookService, bookstoreService)

  }

}