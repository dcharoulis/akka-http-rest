package com.lunatech.utils.jwt

import java.util.UUID

import com.lunatech.errors.ServiceError
import com.lunatech.errors.ServiceError.AuthenticationError
import pdi.jwt.{Jwt, JwtAlgorithm, JwtCirce, JwtClaim}

import scala.util.{Failure, Success, Try}

object JWTUtils {

  private val tokenPrefix = "Bearer "
  val secretKey = "super_secret_key"
  private val algorithm = JwtAlgorithm.HS256
  private val acceptedAlgorithms = Seq(algorithm)
  private val accessTokenExpiration = 1
  private val refreshTokenExpiration = 60 * 60 * 6

  def getAccessToken(userId: UUID): String = {
    // Encode from case class, header automatically generated
    // Set that the token has been issued now and expires in 10 seconds
    val jwtClaim: JwtClaim = JwtClaim(subject = Some(userId.toString)).issuedNow.expiresIn(accessTokenExpiration)
    val jwtToken = Jwt.encode(jwtClaim, secretKey, JwtAlgorithm.HS256)
    s"$tokenPrefix$jwtToken"
  }

  def getRefreshToken(userId: UUID): String = {
    val jwtClaim: JwtClaim = JwtClaim(subject = Some(userId.toString)).issuedNow.expiresIn(refreshTokenExpiration)
    val jwtToken = Jwt.encode(jwtClaim, secretKey, JwtAlgorithm.HS256)
    s"$tokenPrefix$jwtToken"
  }

  def validateToken(token: String): Either[AuthenticationError, Boolean] = {
    // If you only want to check if a token is valid without decoding it.
    // All good
    //    Jwt.validate(accessToken, secretKey, Seq(JwtAlgorithm.HS256))
    extractTokenBody(token) match {
      case Right(extractedToken) => tokenIsValid(extractedToken)
      case Left(_) => Left(AuthenticationError())
    }
  }

  def decodeToken(token: String): Either[AuthenticationError, Claims] = {
    val extractedToken: Either[AuthenticationError, String] = extractTokenBody(token)
    Jwt.decode(extractedToken.right.get, secretKey, acceptedAlgorithms) match {
      case Failure(_) => Left(AuthenticationError())
      case Success(jwtClaim) =>
        Claims(JwtCirce.parseClaim(jwtClaim)) match {
          case Left(error) => Left(error)
          case Right(claims) =>
            Right(claims)
        }
    }
  }

  def extractClaims(token: String): Option[Claims] = {
    JwtCirce.decode(token, secretKey, Seq(algorithm)).toOption.flatMap { c =>
      for {
        userId <- c.subject.flatMap(s => Try(UUID.fromString(s.toString)).toOption)
        expiration <- c.expiration.filter(_ > currentTimeSeconds)
        issuedAt <- c.issuedAt.filter(_ <= System.currentTimeMillis())
      } yield Claims(userId, issuedAt, expiration)
    }
  }

  private def currentTimeSeconds: Long = System.currentTimeMillis() / 1000

  def tokenIsValid(token: String): Either[ServiceError.AuthenticationError, Boolean] = {
    if (Jwt.isValid(token, secretKey, Seq(JwtAlgorithm.HS256))) {
      Right(true)
    } else {
      Left(AuthenticationError())
    }
  }

  def extractTokenBody(token: String): Either[AuthenticationError, String] = token match {
    case tok if tok.startsWith(tokenPrefix) =>
      Right(tok.substring(7))
    case _ => Left(AuthenticationError())
  }

}
