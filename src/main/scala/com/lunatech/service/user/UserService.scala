package com.lunatech.service.user

import java.util.UUID

import com.lunatech.errors.DatabaseError

import scala.concurrent.Future

trait UserService {

  //  def getUsers: Future[List[UserDto]]
  def getUsers: Future[Either[DatabaseError, List[UserDto]]]

  def getUser(userId: UUID): Future[Either[DatabaseError, UserDto]]

  //  def createUser(userCreate: UserCreate): Future[UserDto]

  def createUser(userCreate: UserCreate): Future[Either[DatabaseError, UserDto]]

  def updateUser(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]]

  def updateUserPartially(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, UserDto]]

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]]

}
