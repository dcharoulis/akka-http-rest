package com.lunatech.service.book

sealed abstract class BooksEndpointView(val view: String)

object BooksEndpointView {

  case object BookCompact
    extends BooksEndpointView(
      view = "compact"
    )

  case object BookNormal
    extends BooksEndpointView(
      view = "normal"
    )

  def parse(view: String): Option[BooksEndpointView] = {
    view match {
      case BookCompact.view => Some(BookCompact)
      case BookNormal.view => Some(BookNormal)
      case _ => None
    }
  }

}
