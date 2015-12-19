package net.tedstein.rope


object Dimensions {
  case class Position(x: Double, y: Double, z: Double) {
    def add(vel: Velocity, elapsed: Double): Position = {
      val newX = this.x + (vel.x * elapsed)
      val newY = this.y + (vel.y * elapsed)
      val newZ = this.z + (vel.z * elapsed)
      Position(newX, newY, newZ)
    }
  }

  case class Velocity(x: Double, y: Double, z: Double)

  val Origin = Position(0.0, 0.0, 0.0)
  val Stationary = Velocity(0.0, 0.0, 0.0)
}