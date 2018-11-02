package com.lunatech.service.book


sealed abstract class BookGenreEndpointView(val value: String)

object BookGenreEndpointView {

  case object All
    extends BookGenreEndpointView(
      value = "all"
    )

  case object Horror
    extends BookGenreEndpointView(
      value = "Horror"
    )

  case object Mystery
    extends BookGenreEndpointView(
      value = "Mystery"
    )

  case object Unknown
    extends BookGenreEndpointView(
      value = "Unknown"
    )

  def parse(status: String): Option[BookGenreEndpointView] = {
    status match {
      case Horror.value => Some(Horror)
      case Mystery.value => Some(Mystery)
      case All.value => Some(All)
      case _ => Some(Unknown)
    }
  }

}
