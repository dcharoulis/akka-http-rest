package com.lunatech.service.auth

import com.lunatech.errors.DatabaseError
import com.lunatech.service.user._
import com.lunatech.service.user.persistence.UserPersistence
import com.lunatech.utils.jwt.JWTUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthServiceDefault(val userPersistence: UserPersistence) extends AuthService {

  // TODO: remove type
  def loginUser(userLogin: UserLogin): Future[Either[DatabaseError, UserLoginDto]] = {
    userPersistence.loginUser(userLogin.email, userLogin.password).map {
      case Right(user) =>
        val refreshToken = getRefreshToken(user.userId)
        val accessToken = getAccessToken(user.userId)
        Right(UserLoginDto(accessToken, refreshToken, expiresIn = 11, "Bearer"))
      case Left(error) => Left(error)
    }
  }

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.createUser(userRegister).map {
      case Right(value) => Right(UserDto.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

}
