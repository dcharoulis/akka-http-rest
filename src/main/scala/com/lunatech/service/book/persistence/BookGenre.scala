package com.lunatech.service.book.persistence

sealed abstract class BookGenre(val genre: String)

object BookGenre {

  case object MYSTERY
    extends BookGenre(
      genre = "Mystery"
    )

  case object HORROR
    extends BookGenre(
      genre = "Horror"
    )

//  val all = scala.collection.immutable.Seq(MYSTERY, HORROR)
//
//  private val _databaseCodeMap = all.map(t => t.genre -> t).toMap
//
//  def apply(databaseCode: String) = _databaseCodeMap(databaseCode)

}
