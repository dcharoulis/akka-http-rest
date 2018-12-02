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

}
