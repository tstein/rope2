package net.tedstein.rope.physics

import scala.math.{pow, sqrt}
//import Array._

object Dimensions {
  case class Position(x: Double, y: Double, z: Double) {
    def add(pos: Position): Position = {
      val newX = this.x + pos.x
      val newY = this.y + pos.y
      val newZ = this.z + pos.z
      Position(newX, newY, newZ)
    }

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
    //Best way to think about the operation: take this and boost it by boostVel
    def add(boostVel: Velocity): Velocity = {
      val dotProduct = this.dot(boostVel)
      def f(lhs: Double, rhs: Double): Double = {
        return (rhs + lhs / boostVel.gamma + dotProduct * rhs * boostVel.gamma / (1 + boostVel.gamma))/(1 + dotProduct)
      }
      Velocity(f(this.x,boostVel.x),f(this.y,boostVel.y),f(this.z,boostVel.z))
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
  val Epoch = 0.0
  val LightSecond = 1.0
  def Empty[T] = Set[T]()
}