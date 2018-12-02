package com.lunatech.service.user.persistence

import java.util.UUID

import com.lunatech.persistence.SlickJdbcProfile
import com.lunatech.service.user.User
import slick.lifted.ProvenShape

trait UserTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class UserTable(tag: Tag) extends Table[User](tag, "Users") {

    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def userId: Rep[UUID] = column[UUID]("user_id", O.Unique)

    def email: Rep[String] = column[String]("email", O.Unique)

    def password: Rep[String] = column[String]("password")

    def firstName: Rep[String] = column[String]("first_name")

    def lastName: Rep[String] = column[String]("last_name")

    def balance: Rep[BigDecimal] = column[BigDecimal]("balance")

    def * : ProvenShape[User] = (
      id.?,
      userId,
      email,
      password,
      firstName,
      lastName,
      balance
    ) <> ((User.apply _).tupled, User.unapply)
  }

  //  def fkAuthor = foreignKey("fk_author_id", authorId, Tables.authors)(_.id, onDelete = ForeignKeyAction.Cascade)

}
