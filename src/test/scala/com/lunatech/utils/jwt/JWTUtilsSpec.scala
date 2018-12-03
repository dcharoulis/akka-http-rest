package com.lunatech.utils.jwt

import java.util.UUID

import cats.Applicative
import cats.data.{Validated, ValidatedNel}
import cats.implicits._
import com.lunatech.service.user.UserCreate
import com.lunatech.utils.validators.ObjectsValidators
import org.scalatest.FunSpecLike

class JWTUtilsSpec extends FunSpecLike {

  val userId: UUID = UUID.randomUUID()

  describe("JWT") {
    it("Successfully generate an Access Token") {
      val accessToken = JWTUtils.getAccessToken(userId)
      assert(accessToken.startsWith("Bearer "))
    }


    it("validator") {

      val user = UserCreate("12345678900", "123", "", "")

      type ApiValidation[+A] = ValidatedNel[String, A]

      def validate(userCreate: UserCreate): ApiValidation[UserCreate] =
        Applicative[ApiValidation].map2(
          maxLength(10)(userCreate.email),
          maxLength(2)(userCreate.firstName)
        )((_, _) => userCreate)

      def maxLength(int: Int)(string: String): ApiValidation[String] =
        Validated.condNel(string.length <= int, string, s"$string must be less or equal to $int characters")

      validate(user) match {
        case Validated.Valid(a) =>
          println(a)
        case Validated.Invalid(e) =>
          println(e)
      }

      succeed
    }

    it("valid") {

      val user = UserCreate("pkont@gmail.com", "pas132!swordP", "Pe#@#-tros", "Kontogiannis")

      ObjectsValidators.validateUserCreate(user) match {
        case Validated.Valid(a) =>
          println(a)
        case Validated.Invalid(e) =>
          println(e.get(1).get.errorMessage)
          e.get(1)
      }
      succeed
    }
  }

}
