package com.lunatech.service.order

import java.util.UUID

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Route
import com.lunatech.errors.{ErrorMapper, ErrorResponse, HttpError, ServiceError}
import com.lunatech.service.Routes
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class OrderRoutes(val orderService: OrderService) extends Routes {

  val orderRoutes: Route = internal.routes

  private object internal {

    implicit val httpErrorMapper: ErrorMapper[ServiceError, HttpError] =
      Routes.buildErrorMapper(ServiceError.httpErrorMapper)

    implicit class ErrorOps[E <: ServiceError, A](result: Future[Either[E, A]]) {
      def toRestError[G <: HttpError](implicit errorMapper: ErrorMapper[E, G]): Future[Either[G, A]] = result.map {
        case Left(error) => Left(errorMapper(error))
        case Right(value) => Right(value)
      }
    }

    def completeEither[E <: ServiceError, R: ToEntityMarshaller]
    (statusCode: StatusCode, either: => Either[E, R])(
      implicit mapper: ErrorMapper[E, HttpError]
    ): Route = {
      either match {
        case Left(value) => complete(statusCode, ErrorResponse(code = value.code, message = value.message))
        case Right(value) => complete(statusCode, value)
      }
    }

    def routes: Route = {
      pathPrefix(version)(
        orderManagement
      )
    }

    def orderManagement: Route =
      pathPrefix("orders") {
        orderActions ~ postOrder ~ getOrders
      }

    def getOrders: Route = ???

    def postOrder: Route = ???

    def orderActions: Route =
      pathPrefix(Segment) { id =>
        val orderId = UUID.fromString(id)
        getOrder(orderId)
      }

    def getOrder(orderId: UUID): Route =
      get(
        onComplete(orderService.getOrder(orderId)) {
          case Success(future) =>
            completeEither(StatusCodes.OK, future)
          case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
        }
      )

  }


}
