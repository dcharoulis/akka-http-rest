name := "rest-api-workshop"
organization := "com.kontogiannis"

lazy val akkaHttpVersion = "10.1.4"
lazy val akkaVersion = "2.5.17"
lazy val scalaTestVersion = "3.0.5"
lazy val argonautVersion = "6.2.2"
lazy val slickVersion = "3.2.3"
lazy val sqliteJdbcVersion = "3.25.2"
lazy val flywayVersion = "5.2.0"
lazy val mockServerVersion = "5.4.1"
lazy val jwtVersion = "0.19.0"

lazy val circeVersion = "0.10.0"
lazy val circeExtra = "1.22.0"
lazy val h2Version = "1.3.148"
lazy val catsVersion = "1.4.0"

scalaVersion in ThisBuild := "2.12.7"
scalaSource in Test := baseDirectory.value / "it"

lazy val root = (project in file(".")).

  settings(

    version := "0.1",

    resolvers += Resolver.bintrayRepo("unisay", "maven"),

    libraryDependencies ++=
      Seq(
        // Akka HTTP
        "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
        "com.typesafe.akka" %% "akka-stream" % akkaVersion,

        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
        "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,

        // Scala Test
        "org.scalatest" %% "scalatest" % scalaTestVersion % Test,

        // JSON Serialization Library
        "io.circe" %% "circe-core" % circeVersion,
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "de.heikoseeberger" %% "akka-http-circe" % circeExtra,

        // Migration of SQL Databases
        "org.flywaydb" % "flyway-core" % flywayVersion,

        // ORM
        "com.typesafe.slick" %% "slick" % slickVersion,
        //        "org.xerial" % "sqlite-jdbc" % sqliteJdbcVersion,
        "com.h2database" % "h2" % h2Version, // % Test,

        // Mock server
        //        "org.mock-server" % "mockserver-client-java" % mockServerVersion,

        // Logging dependencies
        //        "org.slf4j" % "slf4j-nop" % "1.6.4",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",

        "com.pauldijou" %% "jwt-core" % jwtVersion,
        "com.pauldijou" %% "jwt-circe" % jwtVersion,

        "com.github.unisay" %% "mockserver-client-scala" % "0.3.0",

        "org.typelevel" %% "cats-core" % catsVersion
      )
  )

enablePlugins(UniversalPlugin)
enablePlugins(DockerPlugin)
