package com.lunatech.service.user

import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.GenericDatabaseError
import com.lunatech.service.user.persistence.UserPersistence
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class UserServiceDefault(val userPersistence: UserPersistence)
  extends UserService with LazyLogging {

  def getUsers: Future[Either[DatabaseError, List[UserDto]]] =
    userPersistence.getUsers.map {
      case Right(value) => Right(value.map(user => UserDto.userToUserDto(user)).toList)
      case Left(_) => Left(GenericDatabaseError)
    }

  def getUser(userId: UUID): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.getUser(userId).map {
      case Right(value) => Right(UserDto.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

  def createUser(userCreate: UserCreate): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.createUser(userCreate).map {
      case Right(value) => Right(UserDto.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

  def updateUser(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]] = {
    userPersistence.updateUser(userId, updateUser).map {
      case Right(value) => Right(UserDto.userToUserDto(value))
      case Left(error) => Left(error)
    }
  }

  def updateUserPartially(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]] =
    userPersistence.updateUserPartially(userId, updateUser).map {
      case Right(value) => Right(UserDto.userToUserDto(value))
      case Left(error) => Left(error)
    }

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]] =
    userPersistence.deleteUser(userId).map {
      case Right(value) => Right(value)
      case Left(error) => Left(error)
    }
}
