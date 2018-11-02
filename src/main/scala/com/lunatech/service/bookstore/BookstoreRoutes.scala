package com.lunatech.service.bookstore

import java.sql.Timestamp
import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.server.Route
import com.lunatech.errors.{ErrorMapper, ErrorResponse, HttpError, ServiceError}
import com.lunatech.service.Routes
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookstoreRoutes(val bookstoreService: BookstoreService) extends Routes {

  val bookstoreRoutes: Route = internal.routes

  private object internal {

    implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
      Routes.buildErrorMapper(ServiceError.httpErrorMapper)

    implicit class ErrorOps[E <: ServiceError, A](result: Future[Either[E, A]]) {
      def toRestError[G <: HttpError](implicit errorMapper: ErrorMapper[E, G]): Future[Either[G, A]] = result.map {
        case Left(error) => Left(errorMapper(error))
        case Right(value) => Right(value)
      }
    }

    implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
      override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

      override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
    }

    def routes: Route = {
      pathPrefix(version)(
        bookstoreManagement
      )
    }

    def bookstoreManagement: Route =
      pathPrefix("bookstores") {
        bookstoreActions ~ postBookstore ~ getBookstores
      }

    def getBookstores: Route = ???

    def postBookstore: Route = ???

    def bookstoreActions: Route =
      pathPrefix(Segment) { id =>
        val bookstoreId = UUID.fromString(id)
        getBookstore(bookstoreId) ~ bookstoreBooks(bookstoreId)
      }

    def getBookstore(bookstoreId: UUID): Route = ???

    def bookstoreBooks(bookstoreId: UUID): Route =
      pathPrefix("books") {
        addBookstoreBook(bookstoreId) ~ getBookstoreBooks(bookstoreId) ~ bookstoreBooksActions(bookstoreId)
      }

    def getBookstoreBooks(bookstoreId: UUID): Route = ???

    def addBookstoreBook(bookstoreId: UUID): Route = ???

    def bookstoreBooksActions(bookstoreId: UUID): Route =
      pathPrefix(Segment) { id =>
        val bookId = UUID.fromString(id)
        updateBookstoreBook(bookstoreId, bookId)
      }

    def updateBookstoreBook(bookstoreId: UUID, bookId: UUID): Route = ???

    def completeEither[E <: ServiceError, R: ToEntityMarshaller]
    (statusCode: StatusCode, either: => Either[E, R])(
      implicit mapper: ErrorMapper[E, HttpError]
    ): Route = {
      either match {
        case Right(value) => complete(value)
        case Left(value) => complete(value.statusCode, ErrorResponse(code = value.code, message = value.message))
      }
    }

  }

}
