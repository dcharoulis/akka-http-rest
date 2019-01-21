package com.lunatech.utils.server

import java.util.UUID

import akka.http.scaladsl.model.Uri.Path

case class Paths(version: ServiceVersion) {

  def root: Path = Path("/" + version.value)

  object public {

    def users: Path =
      root / "users"

    def user(userId: UUID): Path =
      users / userId.toString
  }

  object merchantApp {

    def officialAccounts: Path =
      root / "official-accounts"

    def orders(merchantId: String): Path =
      officialAccounts / merchantId / "orders"

    def order(merchantId: String, orderId: UUID): Path =
      officialAccounts / merchantId / "orders" / orderId.toString
  }

  object internal {

    def ms: Path =
      root / "ms"

    def orders: Path =
      ms / "orders"

    def order(orderId: UUID): Path =
      orders / orderId.toString
  }

}
