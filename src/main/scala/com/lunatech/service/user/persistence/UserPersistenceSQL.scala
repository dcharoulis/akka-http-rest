package com.lunatech.service.user.persistence

import java.util.UUID

import com.lunatech.errors.ServiceError.{GenericDatabaseError, OrderNotFound, RecordAlreadyExists, RecordNotFound}
import com.lunatech.errors.{DatabaseError, ServiceError}
import com.lunatech.service.user.{UpdateUser, User, UserCreate, UserDto}
import com.lunatech.utils.database.DBAccess

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class UserPersistenceSQL(val dbAccess: DBAccess) extends UserPersistence {

  import dbAccess._
  import dbAccess.profile.api._

  private type UserSelection = Query[UserTable, User, Seq]

  //  def getUsers: Future[Seq[User]] = {
  def getUsers: Future[Either[DatabaseError, Seq[User]]] = {
    db.run(Users.result).
      transformWith {
        case Success(users) => Future.successful(Right(users))
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  def createUser(data: UserCreate): Future[Either[DatabaseError, User]] = {
    val userRow = User(userId = UUID.randomUUID(),
      email = data.email, password = data.password,
      firstName = data.firstName, lastName = data.lastName, balance = 0)

    val userAction =
      Users
        .filter(_.email === data.email).result.headOption.flatMap {
        case Some(_) =>
          //          mylog("product was there: " + product)
          //          DBIO.successful(res)
          DBIO.failed(new RuntimeException)
        case None =>
          //          mylog("inserting product")
          (Users returning Users.map(_.id) into (
            (user, newId) => user.copy(id = Some(newId)))
            ) += userRow
      }
        .transactionally

    db.run(userAction).
      transformWith {
        case Success(user) => Future.successful(Right(user))
        case Failure(_) => Future.successful(Left(RecordAlreadyExists))
      }
  }

  def isExists(email: String): Future[Boolean] = db.run {
    Users.filter(_.email === email).exists.result
  }

  def getUser(userId: UUID): Future[Either[DatabaseError, User]] = {
    db.run(getUserQuery(userId)).
      transformWith {
        case Success(optUser) => optUser match {
          case Some(user) => Future.successful(Right(user))
          case None => Future.successful(Left(RecordNotFound))
        }
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  def loginUser(email: String, password: String): Future[Either[DatabaseError, User]] = {
    db.run(Users.filter(user =>
      user.email === email && user.password === password
    ).result.headOption).
      transformWith {
        case Success(optUser) => optUser match {
          case Some(user) => Future.successful(Right(user))
          case None => Future.successful(Left(RecordNotFound))
        }
        case Failure(_) => Future.successful(Left(GenericDatabaseError))
      }
  }

  def updateUser(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] = {
    val updatedUser = User.updateUserToUser(userId, updateUser)
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map(_ => getUserOf(userId).update(updatedUser))
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.headOption
    } yield us

    db.run(actions.transactionally).transformWith {
      case Success(optUser) => optUser match {
        case Some(user) => Future.successful(Right(user))
        case None => Future.successful(Left(RecordNotFound))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  def updateUserPartially(userId: UUID, updateUser: UpdateUser): Future[Either[DatabaseError, User]] = {
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map(oldUser => {
        val updatedUser = User.updateUserRow(oldUser, updateUser)
        getUserOf(userId).update(updatedUser)
      })
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.headOption
    } yield us
    db.run(actions.transactionally).transformWith {
      case Success(optUser) => optUser match {
        case Some(user) => Future.successful(Right(user))
        case None => Future.successful(Left(RecordNotFound))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  def editUser(userId: UUID, updateUser: UpdateUser): Future[User] = {
    val updatedUser = User.updateUserToUser(userId, updateUser)
    val actions = for {
      userOpt <- getUserOf(userId).result.headOption
      updateActionOption = userOpt.map(_ => getUserOf(userId).update(updatedUser))
      _ <- updateActionOption.getOrElse(DBIO.successful(0))
      us <- getUserOf(userId).result.head
    } yield us
    db.run(actions.transactionally)
  }

  def deleteUser(userId: UUID): Future[Either[DatabaseError, Boolean]] = {
    db.run(getUserOf(userId).delete).map {
      case 0 => false
      case 1 => true
      case _ => false
    }

    db.run(getUserOf(userId).delete).transformWith {
      case Success(res) => res match {
        case 0 => Future.successful(Right(false))
        case 1 => Future.successful(Right(true))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }

  private def getUserQuery(userId: UUID): DBIO[Option[User]] = {
    getUserOf(userId).result.headOption
  }

  private def getUserOf(userId: UUID): UserSelection =
    Users.filter(_.userId === userId)

  def testUser: Future[Either[OrderNotFound, Option[UserDto]]] = {
    val use: Future[Option[User]] = db.run(
      Users.filter(_.id === 12).result.headOption
    )
    use.map {
      case Some(uss) => Right(Some(uss))
      case None => Left(ServiceError.OrderNotFound(UUID.randomUUID()))
    }
  }

  def deleteAllUsers: Future[Either[DatabaseError, Boolean]] = {
    db.run(Users.delete).transformWith {
      case Success(res) => res match {
        case 0 => Future.successful(Right(false))
        case 1 => Future.successful(Right(true))
      }
      case Failure(_) => Future.successful(Left(GenericDatabaseError))
    }
  }
}
