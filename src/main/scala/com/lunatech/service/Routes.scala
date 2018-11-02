package com.lunatech.service

import akka.http.scaladsl.server.{Directives, Route, RouteConcatenation}
import com.lunatech.errors._
import com.lunatech.service.auth.AuthRoutes
import com.lunatech.service.book.BookRoutes
import com.lunatech.service.bookstore.BookstoreRoutes
import com.lunatech.service.user.UserRoutes
import com.lunatech.utils.server.Version

trait Routes extends Version with Directives with RouteConcatenation with SecuredRoutes

object Routes extends Directives {

  def buildRoutes(dependencies: Dependencies): Route =
    new AuthRoutes(dependencies.authService).authRoutes ~
      new UserRoutes(dependencies.userService).userRoutes ~
      new BookRoutes(dependencies.bookService).bookRoutes ~
      new BookstoreRoutes(dependencies.bookstoreService).bookstoreRoutes

  val baseErrorMapper: PartialFunction[ServiceError, HttpError] = {
    val db = implicitly[ErrorMapper[DatabaseError, HttpError]]
    ServiceError.httpErrorMapper orElse {
      case e: DatabaseError => db(e)
    }
  }

  def buildErrorMapper(serviceErrorMapper: PartialFunction[ServiceError, HttpError]): ErrorMapper[ServiceError, HttpError] =
    (e: ServiceError) =>
      (serviceErrorMapper orElse baseErrorMapper)
        .applyOrElse(e, (_: ServiceError) => InternalErrorHttp("Unexpected error"))

}
