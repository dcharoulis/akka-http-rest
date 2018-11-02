package com.lunatech.service

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, optionalHeaderValueByName, _}
import com.lunatech.errors._
import com.lunatech.utils.jwt.JWTUtils._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}

trait SecuredRoutes extends FailFastCirceSupport {
  implicit val encodeFoo: Encoder[UnauthorizedErrorHttp] = (a: UnauthorizedErrorHttp) => Json.obj(
    ("foo", Json.fromString(a.code)),
    ("bar", Json.fromString(a.message))
  )
  case class Claims(userId: String, issuedAt: Long)

  def authenticated: Directive1[Map[String, Any]] = {
    optionalHeaderValueByName("Authorization").flatMap {
      case Some(jwt) =>
        validateToken(jwt) match {
          case Left(error) =>
            complete(StatusCodes.Unauthorized, ErrorMapper.toHttpError(error))
          case Right(_) =>
            decodeToken(jwt) match {
              case Left(error) => complete(StatusCodes.Unauthorized, ErrorMapper.toHttpError(error))
              case Right(claims) =>
                provide(Map("user" -> claims.subject))
            }
        }
      case _ => complete(StatusCodes.Unauthorized -> "Token expired with empr.")
    }
  }
}













