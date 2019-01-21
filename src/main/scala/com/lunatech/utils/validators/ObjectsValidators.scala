package com.lunatech.utils.validators

import cats.implicits._
import com.lunatech.service.user.UserCreate

sealed trait ObjectsValidators {

  def validateUserCreate(userCreate: UserCreate): InputValidators.ValidationResult[UserCreate] = {
    (InputValidators.validateEmail(userCreate.email),
      InputValidators.validatePassword(userCreate.password),
      InputValidators.validateFirstName(userCreate.firstName),
      InputValidators.validateLastName(userCreate.lastName)).mapN(UserCreate)
  }

}

object ObjectsValidators extends ObjectsValidators