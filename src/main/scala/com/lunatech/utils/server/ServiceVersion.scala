package com.lunatech.utils.server

sealed abstract class ServiceVersion(val value: String) {
  override def toString: String = value
}

object ServiceVersion {
  case object V01 extends ServiceVersion("v01")
  case object V02 extends ServiceVersion("v02")
}
