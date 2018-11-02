package com.lunatech.persistence

import akka.actor.ActorSystem
import com.lunatech.utils.database.DBAccess
import slick.jdbc.JdbcProfile

trait SlickJdbcProfile {
  val actorSystem: ActorSystem


  lazy val dbAccess = DBAccess(actorSystem)

  lazy val profile: JdbcProfile = slick.jdbc.H2Profile

}
