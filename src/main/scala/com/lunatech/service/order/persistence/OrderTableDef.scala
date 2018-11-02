package com.lunatech.service.order.persistence

import java.util.UUID

import com.lunatech.persistence.SlickJdbcProfile
import com.lunatech.service.order.Order
import slick.lifted.ProvenShape

trait OrderTableDef {
  self: SlickJdbcProfile =>

  import profile.api._

  class OrderTable(tag: Tag) extends Table[Order](tag, "Orders") {

    def id: Rep[Option[Int]] = column[Option[Int]]("id", O.PrimaryKey, O.AutoInc)

    def orderId: Rep[UUID] = column[UUID]("order_id")

    def totalPrice: Rep[Double] = column[Double]("total_price")

    def status: Rep[String] = column[String]("status")

    def userId: Rep[Int] = column[Int]("user_id")

    def deliveryAddress: Rep[String] = column[String]("delivery_address")

    def comments: Rep[String] = column[String]("comments")

    //    def products: Rep[Seq[Int]] = column[Seq[Int]]("products")

    def * : ProvenShape[Order] = (
      id,
      orderId,
      userId,
      totalPrice,
      status,
      deliveryAddress,
      comments
    ) <> (Order.tupled, Order.unapply)
  }

}
