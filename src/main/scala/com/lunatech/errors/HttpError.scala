package com.lunatech.errors

import akka.http.scaladsl.model.StatusCode
import akka.http.scaladsl.model.StatusCodes._

sealed trait HttpError extends Serializable {
  val statusCode: StatusCode
  val code: String
  val message: String
}

case class UnauthorizedErrorHttp() extends HttpError {
  val statusCode: StatusCode = Unauthorized
  val code: String = "Unauthorized"
  val message: String = "Unauthorized"
}

case class EmailAlreadyExistsHttp(email: String) extends HttpError {
  val statusCode: StatusCode = Conflict
  override val code: String = "emailAlreadyExists"
  override val message: String = s"The email $email already exists."
}

case class GenericErrorHttp() extends HttpError {
  val statusCode: StatusCode = ServiceUnavailable
  val code: String = "GenericError"
  val message: String = "GenericError"
}

case class RecordNotFoundErrorHttp() extends HttpError {
  val statusCode: StatusCode = NotFound
  val code: String = "RecordNotFoundError"
  val message: String = "RecordNotFoundError"
}

case class MalformedRequestErrorHttp(message: String) extends HttpError {
  val statusCode: StatusCode = BadRequest
  val code: String = "malformedRequestError"
}

case class InternalErrorHttp(message: String) extends HttpError {
  override val statusCode: StatusCode = InternalServerError
  override val code: String = "internalError"
}

case object DefaultNotFoundErrorHttp extends HttpError {
  override val statusCode: StatusCode = NotFound
  override val code = "DefaultNotFoundErrorHttp"
  override val message = "Can't find requested asset"
}

case class RecordAlreadyExistsHttp(message: String) extends HttpError {
  override val statusCode: StatusCode = Conflict
  override val code = "RecordAlreadyExistsHttp"
}

//case class NotFoundError(message: String) {
//  val statusCode: StatusCode = NotFound
//  val code: String = "malformedRequestError"
//}

trait NotFoundError extends HttpError {
  val statusCode: StatusCode = NotFound
}
