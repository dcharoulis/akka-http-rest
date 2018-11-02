package com.lunatech.service.book

import java.sql.Timestamp
import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import com.lunatech.errors.ServiceError.ViewIsNotDefined
import com.lunatech.errors.{ErrorResponse, ServiceError, _}
import com.lunatech.service.Routes
import com.lunatech.service.book.BooksEndpointView.{BookCompact, BookNormal}
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BookRoutes(val bookService: BookService) extends Routes {

  val bookRoutes: Route = internal.routes

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

    def completeEither[E <: ServiceError, R: ToEntityMarshaller]
    (statusCode: StatusCode, either: => Either[E, R])(
      implicit mapper: ErrorMapper[E, HttpError]
    ): Route = {
      either match {
        case Left(value) => complete(value.statusCode, ErrorResponse(code = value.code, message = value.message))
        case Right(value) => complete(value)
      }
    }

    def routes: Route = {
      pathPrefix(version)(
        bookManagement
      )
    }

    def bookManagement: Route =
      pathPrefix("books") {
        bookActions ~ postBook ~ getBooks
      }

    def bookActions: Route =
      pathPrefix(Segment) { id =>
        val bookId = UUID.fromString(id)
        getBook(bookId)
      }

    def postBook: Route =
      post {
        entity(as[BookCreate]) { bookCreate =>
          onComplete(bookService.createBook(bookCreate)) {
            case Success(future) =>
              completeEither(StatusCodes.Created, future)
            case Failure(ex) =>
              complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
          }
        }
      }

    def getBooks: Route = ???

    def getBook(bookId: UUID): Route = ???
  }

}

