package com.lunatech.utils.validators

import cats.data.ValidatedNec
import cats.implicits._
import com.lunatech.errors._
import com.lunatech.service.user.UserCreate

sealed trait FormValidatorNec {

  type ValidationResult[A] = ValidatedNec[DomainValidation, A]

  private def validateEmail(email: String): ValidationResult[String] =
    if (email.matches("^[a-zA-Z0-9]+$")) email.validNec else EmailHasSpecialCharacters.invalidNec

  private def validatePassword(password: String): ValidationResult[String] =
    if (password.matches("(?=^.{10,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$")) password.validNec
    else PasswordDoesNotMeetCriteria.invalidNec

  private def validateFirstName(firstName: String): ValidationResult[String] =
    if (firstName.matches("^[a-zA-Z]+$")) firstName.validNec else FirstNameHasSpecialCharacters.invalidNec

  private def validateLastName(lastName: String): ValidationResult[String] =
    if (lastName.matches("^[a-zA-Z]+$")) lastName.validNec else LastNameHasSpecialCharacters.invalidNec


  def validateForm(email: String, password: String, firstName: String, lastName: String, age: Int): ValidationResult[UserCreate] = {
    (validateEmail(email),
      validatePassword(password),
      validateFirstName(firstName),
      validateLastName(lastName)).mapN(UserCreate)
  }
}

object FormValidatorNec extends FormValidatorNec
