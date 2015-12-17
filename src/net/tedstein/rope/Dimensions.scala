package net.tedstein.rope


object Dimensions {
  case class Position(x: Double, y: Double, z: Double)
  case class Velocity(x: Double, y: Double, z: Double)

  val Origin = Position(0.0, 0.0, 0.0)
  val Stationary = Velocity(0.0, 0.0, 0.0)
}