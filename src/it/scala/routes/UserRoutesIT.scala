package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server._
import com.lunatech.errors.ErrorResponse
import com.lunatech.service.user._
import com.lunatech.service.user.persistence.UserPersistenceSQL
import com.lunatech.utils.database.DBAccess
import io.circe.generic.auto._
import io.circe.syntax._
import routes.helpers.ServiceSuite

class UserRoutesIT extends ServiceSuite {

  val user = UserCreate("pkont4@gmail.com", "Petros", "Kontogiannis", "password")
  val user2 = UserCreate("pkont5@gmail.com", "Petros", "Kontogiannis", "password")
  val expectedUser = UserDto(UUID.randomUUID(), "pkont4@gmail.com", "Petros", "Kontogiannis", 0)

  trait Fixture {
    val dbAccess = DBAccess(system)
    val userPersistence = new UserPersistenceSQL(dbAccess)
    userPersistence.deleteAllUsers
    val userService = new UserServiceDefault(userPersistence)
    val userRoutes: Route = new UserRoutes(userService).userRoutes
  }

  "The service" should {

    "successfully creates a user" in new Fixture {
      Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultUser = responseAs[UserDto]
        assert(
          resultUser.email === expectedUser.email
        )
      }
    }

    "successfully handles a user with an existent email" in new Fixture {
      Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }

      Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Conflict)
        val errorResponse = responseAs[ErrorResponse]
        assert(
          errorResponse.code === "RecordAlreadyExists" &&
            errorResponse.message === "This email already exists"
        )
      }
    }

    "successfully validated an invalid user" in new Fixture {
      val user = UserCreate("pkont@gmail.com", "pas132!swordP", "Pe#@#-tros", "Kontogiannis")
      Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        //        status should ===(StatusCodes.Created)
        val resultUser = responseAs[ErrorResponse]
        println(resultUser)
        //        assert(
        //          resultUser.email === expectedUser.email
        //        )
      }
    }

    "successfully serves a user" in new Fixture {
      val resultUser: UserDto = Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }
      Get("/v01/users/" + resultUser.userId) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result = responseAs[UserDto]
        assert(
          result.email === expectedUser.email
        )
      }
    }

    "successfully serves a list of users" in new Fixture {
      Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }
      Post("/v01/users", user2) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Get("/v01/users") ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[UserDto]].length shouldBe 2
      }
    }

    "successfully handles an not-existent user" in new Fixture {
      Get("/v01/users/" + UUID.randomUUID()) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.NotFound)
        val errorResponse = responseAs[ErrorResponse]
        assert(
          errorResponse.code === "DefaultNotFoundError" &&
            errorResponse.message === "Can't find requested asset"
        )
      }
    }

    "successfully updates a user" in new Fixture {
      val updateUser = UpdateUser(
        None, Some("pkont4@gmail.com"), None, None, None, None
      )

      val resultUser: UserDto = Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }

      Put("/v01/users/" + resultUser.userId, updateUser) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result = responseAs[UserDto]
        assert(
          result.firstName === ""
        )
      }
    }

    "successfully partially updates a user" in new Fixture {
      val updateUser = UpdateUser(
        userId = None, email = Some("pkont4@gmail.com"), firstName = Some("Isidor"),
        password = None, lastName = None, balance = None
      )

      val resultUser: UserDto = Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }

      Patch("/v01/users/" + resultUser.userId, updateUser) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result = responseAs[UserDto]
        assert(
          result.firstName === updateUser.firstName.get
        )
      }
    }

    "successfully deletes a user" in new Fixture {
      val resultUser: UserDto = Post("/v01/users", user) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[UserDto]
      }

      Delete("/v01/users/" + resultUser.userId) ~> userRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.NoContent)
      }
    }

  }
}
