package com.lunatech.errors

import java.util.UUID

import com.lunatech.errors.ServiceError.{GenericDatabaseError, RecordAlreadyExists, RecordNotFound}

sealed trait ServiceError

trait DatabaseError extends ServiceError

object DatabaseError {
  implicit val ToHttpErrorMapper: ErrorMapper[DatabaseError, HttpError] = {
    case GenericDatabaseError =>
      InternalErrorHttp("Unexpected error")
    case RecordAlreadyExists =>
      RecordAlreadyExistsHttp("This email already exists")
    case RecordNotFound =>
      DefaultNotFoundErrorHttp
  }
}

object ServiceError {

  case object GenericDatabaseError extends DatabaseError

  case object RecordNotFound extends DatabaseError

  case object RecordAlreadyExists extends DatabaseError

  case class FieldAreNotCorrect(fieldName: String, errorText: String)
    extends ServiceError

  case class AuthenticationError() extends ServiceError

  case class OrderNotFound(orderId: UUID) extends ServiceError

  case class UserNotFound(userId: UUID) extends ServiceError

  case class EmailAlreadyExists(email: String) extends ServiceError

  case class UnableToReservePromocode(offerId: UUID) extends ServiceError

  case class ViewIsNotDefined(view: String) extends ServiceError

  case class InputValidationError(errors: List[String]) extends ServiceError

  val httpErrorMapper: PartialFunction[ServiceError, HttpError] = {

    case AuthenticationError() =>
      new UnauthorizedErrorHttp {
        override val code: String = "authenticationError"
        override val message: String = "authenticationError"
      }

    case EmailAlreadyExists(email) =>
      EmailAlreadyExistsHttp(email)

    case FieldAreNotCorrect(fieldName: String, errorText: String) => {
      val message = fieldName + errorText
      MalformedRequestErrorHttp(message)
    }

    case UserNotFound(userId) =>
      new NotFoundError() {
        override val message: String = s"User with id $userId not found"
        override val code: String = "userNotFound"
      }

    case OrderNotFound(orderId) =>
      new UnauthorizedErrorHttp {
        override val code: String = "orderNotFound"
        override val message: String = s"Order with id $orderId not found"

      }
    case UnableToReservePromocode(offerId) =>
      new UnauthorizedErrorHttp {
        override val code: String = "unableToReservePromocode"
        override val message: String = s"Unable to reserve promocode for offer with id, $offerId"
      }
    case ViewIsNotDefined(view) =>
      new UnauthorizedErrorHttp {
        override val code: String = "unableToRetrieveView"
        override val message: String = s"Unable to retrieve orders with view, $view"
      }
    case InputValidationError(errors) =>
      new MalformedRequestErrorHttp("asd"){
        override val code: String = "inputValidationError"
        override val message: String = s"Input validation error, $errors"
      }
  }
}
