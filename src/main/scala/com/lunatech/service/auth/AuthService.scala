package com.lunatech.service.auth

import com.lunatech.errors.DatabaseError
import com.lunatech.service.user.{UserCreate, UserDto, UserLogin, UserLoginDto}

import scala.concurrent.Future


trait AuthService {

  def loginUser(userLogin: UserLogin): Future[Either[DatabaseError, UserLoginDto]]

  def registerUser(userRegister: UserCreate): Future[Either[DatabaseError, UserDto]]

}
