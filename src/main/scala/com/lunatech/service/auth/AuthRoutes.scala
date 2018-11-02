package com.lunatech.service.auth

import akka.http.scaladsl.marshalling.ToEntityMarshaller
import akka.http.scaladsl.model.{HttpResponse, StatusCode, StatusCodes}
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import com.lunatech.errors.{ErrorMapper, ErrorResponse, HttpError, ServiceError}
import com.lunatech.service.Routes
import com.lunatech.service.user.{UserCreate, UserLogin}
import io.circe.generic.auto._
import io.circe.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class AuthRoutes(val authService: AuthService) extends Routes {

  val authRoutes: Route =
    auth.routes

  private object auth {

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
        case Left(value) => complete(value.statusCode, ErrorResponse(code = value.code, message = value.message))
        case Right(value) => complete(value)
      }
    }

    def routes: Route = {
      pathPrefix(version)(
        authManagement
      )
    }

    def authManagement: Route =
      pathPrefix("auth") {
        register ~ login
      }

    def register: Route = {
      path("register") {
        pathEndOrSingleSlash {
          post {
            entity(as[UserCreate]) {
              userRegister =>
                onComplete(authService.registerUser(userRegister)) {
                  case Success(future) =>
                    completeEither(StatusCodes.Created, future)
                  case Failure(ex) =>
                    complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
                }
            }
          }
        }
      }
    }

    def login: Route = {
      path("login") {
        pathEndOrSingleSlash {
          post {
            entity(as[UserLogin]) { userLogin =>
              onComplete(authService.loginUser(userLogin)) {
                case Success(future) =>
                  completeEither(StatusCodes.Created, future)
                case Failure(ex) =>
                  complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
              }
            }
          }
        }
      }
    }

  }

  val myExceptionHandler = ExceptionHandler {
    case _: ArithmeticException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally")
        complete(HttpResponse(StatusCodes.Unauthorized, entity = "Bad numbers, bad result!!!"))
      }
  }

}
