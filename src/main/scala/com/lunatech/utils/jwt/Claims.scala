package com.lunatech.utils.jwt

import java.util.UUID

import com.lunatech.errors.ServiceError.AuthenticationError
import pdi.jwt.JwtClaim

import scala.util.{Failure, Success, Try}

case class Claims(subject: UUID, issuedAt: Long, expires: Long)

object Claims {
  def apply(jwtClaim: JwtClaim): Either[AuthenticationError, Claims] = {
    Try(new Claims(UUID.fromString(jwtClaim.subject.get), jwtClaim.issuedAt.get, jwtClaim.expiration.get)) match {
      case Failure(_) => Left(AuthenticationError())
      case Success(value) => Right(value)
    }
  }
}
