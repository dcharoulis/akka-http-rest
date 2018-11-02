package com.lunatech.service.book.persistence

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

import com.lunatech.errors.DatabaseError
import com.lunatech.errors.ServiceError.{GenericDatabaseError, RecordNotFound}
import com.lunatech.service.book._
import com.lunatech.service.bookstore.persistence.BookstorePersistence
import com.lunatech.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}


class BookPersistenceSQL(val dbAccess: DBAccess, val bookstorePersistence: BookstorePersistence) extends BookPersistence {

  import dbAccess._
  import dbAccess.profile.api._

  override def getBook(bookId: UUID): Future[Either[DatabaseError, Book]] = {
    db.run(Books.filter(_.bookId === bookId).result.headOption).
      transformWith {
        case Success(optBook) => optBook match {
          case Some(book) => Future.successful(Right(book))
          case None => Future.successful(Left(RecordNotFound))
        }
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def createBook(data: BookCreate): Future[Either[DatabaseError, Book]] = {
    val bookRow = Book(
      bookId = UUID.randomUUID(), title = data.title,
      timestamp = Timestamp.from(Instant.now()),
      genre = data.genre,
      price = data.price, quantity = data.quantity
    )
    val bookAction = (Books returning Books.map(_.id) into (
      (book, newId) => book.copy(id = Some(newId)))
      ) += bookRow
    db.run(bookAction).
      transformWith {
        case Success(user) => Future.successful(Right(user))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def editBook(bookId: UUID, data: Book): Future[Either[DatabaseError, Book]] = ???

  private type BookSelection = Query[BookTable, Book, Seq]

  case class BookSelect[A](a: Option[A], bookSelectable: BookSelectable[A])

  trait BookSelectable[A] {
    def selectBook(bookSelection: BookSelection, a: A): BookSelection
  }

  implicit val bookSelectableByTitle: BookSelectable[String] =
    (bookSelection: BookSelection, title: String) =>
      bookSelection.filter(_.title.toLowerCase like s"%${title.toLowerCase}%")

  implicit val bookSelectableByCreatedBefore: BookSelectable[Timestamp] =
    (bookSelection: BookSelection, timestamp: Timestamp) =>
      bookSelection.filter(_.timestamp < timestamp)

  implicit val bookSelectableByGenre: BookSelectable[BookGenreEndpointView] =
    (bookSelection: BookSelection, genre: BookGenreEndpointView) =>
      genre match {
        case BookGenreEndpointView.All => bookSelection
        case BookGenreEndpointView.Horror => bookSelection.filter(_.genre === genre.value)
        case BookGenreEndpointView.Mystery => bookSelection.filter(_.genre === genre.value)
        case _ => bookSelection.filter(_.genre === genre.value)
      }

  implicit def asBookSelect[A](a: Option[A])(implicit bookSelectable: BookSelectable[A]): BookSelect[A] =
    BookSelect(a, bookSelectable)

  private def getBooks(fields: List[BookSelect[_]]): BookSelection = {
    val bookSelection = fields.foldLeft[BookSelection](Books) {
      case (books, BookSelect(None, _)) => books
      case (books, BookSelect(Some(id), ods)) => ods.selectBook(books, id)
    }
    bookSelection
  }

  override def listBooks(title: Option[String], timestamp: Option[Timestamp],
                         genre: Option[BookGenreEndpointView]): Future[Either[DatabaseError, Seq[Book]]] = {
    db.run(getBooks(List[BookSelect[_]](title, timestamp, genre)).result).
      transformWith {
        case Success(users) => Future.successful(Right(users))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def addBookToBookstore(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]] = {
    db.run(BookstoresBooks += BookstoreBook(
      bookstoreId = bookstoreId, bookId = bookstoreBookDto.bookId, quantity = bookstoreBookDto.quantity)).
      transformWith {
        case Success(bookstore) => Future.successful(Right(bookstore))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def updateBookstoreBook(bookstoreId: UUID, bookstoreBookDto: BookstoreBookDto): Future[Either[DatabaseError, Int]] = {
    val bookstoreRow = BookstoreBook(
      bookstoreId = bookstoreId, bookId = bookstoreBookDto.bookId, quantity = bookstoreBookDto.quantity)
    db.run(BookstoresBooks
      .filter(bb => bb.bookstoreId === bookstoreId && bb.bookId === bookstoreBookDto.bookId)
      .update(bookstoreRow)).
      transformWith {
        case Success(bookstore) => Future.successful(Right(bookstore))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  override def getBookstoreBooks(bookstoreId: UUID): Future[Either[DatabaseError, Seq[Book]]] = {
    val bookAction = BookstoresBooks
      .join(Books)
      .on {
        case (bb, b) =>
          bb.bookId === b.bookId && bb.bookstoreId === bookstoreId
      }
      .map {
        case (bb, b) => (bb, b)
      }.result

    db.run(bookAction).transformWith {
      case Success(value) => Future(Right(value.map(res => Book.bookRowToBook(res._2, res._1.quantity))))
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }
}
