package com.lunatech.errors

import com.lunatech.errors.ServiceError.AuthenticationError

trait ErrorMapper[-FromError <: ServiceError, +ToError <: HttpError] extends (FromError => ToError)

object ErrorMapper {

  implicit val toHttpError: ErrorMapper[AuthenticationError, UnauthorizedErrorHttp] = {
    _ : ServiceError.AuthenticationError => UnauthorizedErrorHttp()
  }
}
