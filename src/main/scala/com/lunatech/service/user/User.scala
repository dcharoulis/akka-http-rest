package com.lunatech.service.user

import java.util.UUID

import com.lunatech.utils.validators.{InputValidators, ObjectsValidators}

import scala.language.{higherKinds, implicitConversions}

case class Usr[F[_]](id: Option[Int],
                     userId: F[UUID],
                     email: F[String],
                     firstName: F[String],
                     lastName: F[String],
                     balance: F[BigDecimal])

case class User(id: Option[Int] = None,
                userId: UUID,
                email: String,
                password: String,
                firstName: String,
                lastName: String,
                balance: BigDecimal)

object User {

  def updateUserToUser(userId: UUID, updateUser: UpdateUser): User =
    User(
      userId = updateUser.userId.getOrElse(userId),
      email = updateUser.email.getOrElse(""),
      password = updateUser.password.getOrElse(""),
      firstName = updateUser.firstName.getOrElse(""),
      lastName = updateUser.lastName.getOrElse(""),
      balance = updateUser.balance.getOrElse(0)
    )

  def updateUserRow(old: User, update: UpdateUser): User =
    old.copy(
      email = update.email.getOrElse(old.email),
      firstName = update.firstName.getOrElse(old.firstName),
      lastName = update.lastName.getOrElse(old.lastName),
      password = update.password.getOrElse(old.password),
      balance = update.balance.getOrElse(old.balance)
    )
}

case class UpdateUser(
                       userId: Option[UUID],
                       password: Option[String],
                       email: Option[String],
                       firstName: Option[String],
                       lastName: Option[String],
                       balance: Option[BigDecimal]
                     )

case class UserDto(userId: UUID,
                   email: String,
                   firstName: String,
                   lastName: String,
                   balance: BigDecimal)

object UserDto {
  implicit def userToUserDto(user: User): UserDto = {
    UserDto(user.userId, user.email, user.firstName, user.lastName, user.balance)
  }
}

case class UserLogin(email: String, password: String)

case class UserLoginDto(accessToken: String, refreshToken: String, expiresIn: Int, tokenType: String)

case class UserCreate(email: String,
                      firstName: String,
                      lastName: String,
                      password: String) {

  def validate: InputValidators.ValidationResult[UserCreate] =
    ObjectsValidators.validateUserCreate(this)

}