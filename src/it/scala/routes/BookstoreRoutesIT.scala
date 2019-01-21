package routes

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.lunatech.service.book.persistence.BookPersistenceSQL
import com.lunatech.service.bookstore.persistence.BookstorePersistenceSQL
import com.lunatech.service.bookstore.{BookstoreCreate, BookstoreDto, BookstoreRoutes, BookstoreServiceDefault}
import com.lunatech.utils.database.DBAccess
import io.circe.generic.auto._
import io.circe.syntax._
import routes.helpers.ServiceSuite

class BookstoreRoutesIT extends ServiceSuite {

  val bookstore = BookstoreCreate("Lunatect", "Rotterdam")
  val bookstore2 = BookstoreCreate("Lunatect", "Rotterdam")
  val expectedBookstore = BookstoreDto(UUID.randomUUID(), "Lunatect", "Rotterdam")

  trait Fixture {
    val dbAccess = DBAccess(system)
    val bookstorePersistence = new BookstorePersistenceSQL(dbAccess)
    val bookPersistence = new BookPersistenceSQL(dbAccess, bookstorePersistence)
    bookPersistence.deleteAllBooks
    val bookstoreService = new BookstoreServiceDefault(bookstorePersistence, bookPersistence)
    val bookstoreRoutes: Route = new BookstoreRoutes(bookstoreService).bookstoreRoutes
  }


  "The service" should {

    "successfully creates a new bookstore" in new Fixture {
      Post("/v01/bookstores", bookstore) ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultBookstore = responseAs[BookstoreDto]
        assert(
          resultBookstore.name === expectedBookstore.name
        )
      }
    }

    "successfully serves a bookstore" in new Fixture {
      val resultBookstore: BookstoreDto = Post("/v01/bookstores", bookstore) ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[BookstoreDto]
      }
      Get("/v01/bookstores/" + resultBookstore.bookstoreId) ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result = responseAs[BookstoreDto]
        assert(
          result.name === expectedBookstore.name
        )
      }
    }

    "successfully serves a list of bookstore" in new Fixture {
      Post("/v01/bookstores", bookstore) ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[BookstoreDto]
      }
      Post("/v01/bookstores", bookstore2) ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Get("/v01/bookstores") ~> bookstoreRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[BookstoreDto]].length shouldBe 2
      }
    }

  }

}
