package routes

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.lunatech.service.book.BooksEndpointView.BookNormal
import com.lunatech.service.book._
import com.lunatech.service.book.persistence.BookPersistenceSQL
import com.lunatech.service.bookstore.persistence.BookstorePersistenceSQL
import com.lunatech.utils.database.DBAccess
import io.circe.Decoder.Result
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.scalatest.Assertion
import routes.helpers.ServiceSuite

class BookRoutesIT extends ServiceSuite {

  val book = BookCreate("BookTitle", "Horror", 10.05, 5)
  val book2 = BookCreate("BookTitle", "Horror", 10.05, 5)
  val expectedBook = BookDto(UUID.randomUUID(), "BookTitle", Timestamp.from(Instant.now), "Horror", 10.05, 5)

  implicit val TimestampFormat: Encoder[Timestamp] with Decoder[Timestamp] = new Encoder[Timestamp] with Decoder[Timestamp] {
    override def apply(a: Timestamp): Json = Encoder.encodeLong.apply(a.getTime)

    override def apply(c: HCursor): Result[Timestamp] = Decoder.decodeLong.map(s => new Timestamp(s)).apply(c)
  }

  trait Fixture {
    val dbAccess = DBAccess(system)
    val bookstorePersistence = new BookstorePersistenceSQL(dbAccess)
    val bookPersistence = new BookPersistenceSQL(dbAccess, bookstorePersistence)
    bookPersistence.deleteAllBooks
    val bookService = new BookServiceDefault(bookPersistence)
    val bookRoutes: Route = new BookRoutes(bookService).bookRoutes

    def postBook(bookCreate: BookCreate): Assertion =
      Post("/v01/books", book2) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
  }

  "The service" should {

    "successfully creates a book" in new Fixture {
      Post("/v01/books", book) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        val resultBook = responseAs[BookDto]
        assert(
          resultBook.title === expectedBook.title
        )
      }
    }

    "successfully serves a user" in new Fixture {
      val resultBook: BookDto = Post("/v01/books", book) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
        responseAs[BookDto]
      }
      Get("/v01/books/" + resultBook.bookId) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        val result = responseAs[BookDto]
        assert(
          result.title === expectedBook.title
        )
      }
    }

    "successfully serves a list of books with compact view" in new Fixture {
      Post("/v01/books", book) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Post("/v01/books", book2) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Get("/v01/books") ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[BookDtoCompact]].length shouldBe 2
      }
    }

    "successfully serves a list of books wit normal view" in new Fixture {
      Post("/v01/books", book) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Post("/v01/books", book2) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.Created)
      }
      Get("/v01/books?" + "view=" + BookNormal.view) ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[BookDto]].length shouldBe 2
      }
    }

    "successfully serves a list of books wit normal view by query" in new Fixture {

      postBook(BookCreate("The girl in the mirror", "Horror", 10.05, 5))
      postBook(BookCreate("Devil's Tribute", "Mystery", 10.05, 5))
      postBook(BookCreate("Clue of the Artificial Tuba", "Mystery", 10.05, 5))
      postBook(BookCreate("Perfect Gun", "Horror", 10.05, 5))
      postBook(BookCreate("Midnight Rose", "Horror", 10.05, 5))

      Get("/v01/books?" + "genre=all") ~> bookRoutes ~> check {
        handled shouldBe true
        status should ===(StatusCodes.OK)
        responseAs[List[BookDtoCompact]].length shouldBe 5
      }
    }



  }
}