package net.tedstein.rope.physics

import scala.math.{pow, sqrt}
//import Array._

object Dimensions {
  case class Position(x: Double, y: Double, z: Double) {
    def add(vel: Velocity, elapsed: Double): Position = {
      val newX = this.x + (vel.x * elapsed)
      val newY = this.y + (vel.y * elapsed)
      val newZ = this.z + (vel.z * elapsed)
      Position(newX, newY, newZ)
    }
  }

  case class Velocity(x: Double, y: Double, z: Double) {
    //Relativistic velocity addition
    //https://en.wikipedia.org/wiki/Lorentz_transformation#Transformation_of_velocities
    //https://en.wikipedia.org/wiki/Velocity-addition_formula
    def add(vel: Velocity): Velocity = {
      val dotProduct = this.dot(vel)
      var newX = vel.x + this.x / vel.gamma + dotProduct * vel.x * vel.gamma / (1 + vel.gamma)
      newX /= (1 + dotProduct)
      var newY = vel.y + this.y / vel.gamma + dotProduct * vel.y * vel.gamma / (1 + vel.gamma)
      newY /= (1 + dotProduct)
      var newZ = vel.z + this.z / vel.gamma + dotProduct * vel.z * vel.gamma / (1 + vel.gamma)
      newZ /= (1 + dotProduct)
      Velocity(newX, newY, newZ)
      //Ideally, check that this velrss is less than 1 unless we are tacheoning
    }
    def dot(vel: Velocity): Double = {
      var ans = vel.x * this.x
      ans += vel.y * this.y
      ans += vel.z * this.z
      ans
    }
    //def toarray: Array[Double] = val Array[this.x, this.y, this.z]
    def velrss: Double = {
      sqrt(List(x, y, z).map(pow(_, 2)).sum)
    }

    def gamma: Double = {
      1 / sqrt(1 - pow(velrss, 2))
    }
  }
  val Origin = Position(0.0, 0.0, 0.0)
  val Stationary = Velocity(0.0, 0.0, 0.0)
}