package com.lunatech.utils.database

import org.flywaydb.core.Flyway

class FlywayMigration {

  private val flyway = new Flyway()
  flyway.setDataSource("", "", "")

  def migrateDatabaseSchema(): Unit = flyway.migrate()

  def dropDatabase(): Unit = flyway.clean()

}
