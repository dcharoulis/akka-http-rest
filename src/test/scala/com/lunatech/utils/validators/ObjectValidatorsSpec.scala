package com.lunatech.utils.validators

import cats.data.{Chain, Validated}
import com.lunatech.errors.{EmailHasSpecialCharacters, PasswordDoesNotMeetCriteria}
import com.lunatech.service.user.UserCreate
import com.lunatech.utils.validators.InputValidators.ValidationResult
import org.scalatest.FunSpecLike

class ObjectValidatorsSpec extends FunSpecLike {


  describe("ObjectValidators") {
    it("Successfully validate a valid UserCreate object") {
      val userCreate: UserCreate = UserCreate("pkontogiannis4@gmail.com", "Password1!", "Petros", "Kontogiannis")

      val validatorResult: ValidationResult[UserCreate] = ObjectsValidators.validateUserCreate(userCreate)
      validatorResult match {
        case Validated.Valid(a) =>
          assert(a === userCreate)
        case Validated.Invalid(_) => ()
      }
    }

    it("Successfully validate a invalid UserCreate object with invalid email") {
      val userCreate: UserCreate = UserCreate("pkontoggmail.com", "Petros", "Kontogiannis", "pas132!swordP")

      val validatorResult: ValidationResult[UserCreate] = ObjectsValidators.validateUserCreate(userCreate)
      validatorResult match {
        case Validated.Valid(_) => ()
        case Validated.Invalid(e) =>
          assert(e === Chain(EmailHasSpecialCharacters))
      }
    }

    it("Successfully validate a invalid UserCreate object with invalid password") {
      val userCreate: UserCreate = UserCreate("pkontog@gmail.com", "Petros", "Kontogiannis", "passwor")

      val validatorResult: ValidationResult[UserCreate] = ObjectsValidators.validateUserCreate(userCreate)
      validatorResult match {
        case Validated.Valid(_) => ()
        case Validated.Invalid(e) =>
          assert(e === Chain(PasswordDoesNotMeetCriteria))
      }
    }

  }

}
