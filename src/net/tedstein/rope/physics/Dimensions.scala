package net.tedstein.rope.physics

import scala.math.{pow, sqrt}
//import Array._

object Dimensions {
  case class Position(v: Vector3d) {
    //Preserve get compatibility with this.x, etc.
    def x: Double = this.v.x
    def y: Double = this.v.y
    def z: Double = this.v.z
    def this(xc: Double, yc: Double, zc: Double) = this(Vector3d(xc, yc, zc))

    def add(pos: Position): Position = Position(pos.v + v)

    def add(vel: Velocity, elapsed: Double): Position = this.add(Position(vel.v * elapsed))

    //Lorentz Transform
    //https://en.wikipedia.org/wiki/Lorentz_transformation#Vector_transformations
    //Note: time is also affected by this.  It is left out here, but still an input.
    //Alternative definition
    /*def lorentzBoost(boostVel: Velocity, time: Double): Position = {
      def f(lhs: Double, gamma: Double, dotProduct: Double, normal: Double, rhs: Double) = {
        lhs + (gamma - 1) * dotProduct * normal - rhs
      }
      val gamma = boostVel.gamma
      val dotProduct = this.dot(boostVel.direction)
      Position(
        f(this.x, gamma, dotProduct, boostVel.direction.x, gamma * time * boostVel.x),
        f(this.y, gamma, dotProduct, boostVel.direction.y, gamma * time * boostVel.y),
        f(this.z, gamma, dotProduct, boostVel.direction.z, gamma * time * boostVel.z)
      )
    }*/
    //Vector definition
    def lorentzBoost(boostVel: Velocity, time: Double): Position = {
      val gamma = boostVel.gamma
      Position(
        this.v +
          boostVel.direction.v * ((gamma - 1) * (this.v * boostVel.direction.v)) -
          boostVel.v * gamma * time
      )
    }
    def dot(pos: Position): Double = v * pos.v
  }
  object Position{
    //This allows a call like the following without "new":
    //Position(x,y,z)
    def apply(xc: Double, yc: Double, zc: Double): Position = Position(Vector3d(xc, yc, zc))
  }

  case class Velocity(v: Vector3d) {
    //Preserve get compatibility with this.x, etc.
    def x: Double = this.v.x
    def y: Double = this.v.y
    def z: Double = this.v.z
    def this(xc: Double, yc: Double, zc: Double) = this(Vector3d(xc, yc, zc))

    //Relativistic velocity addition
    //https://en.wikipedia.org/wiki/Lorentz_transformation#Transformation_of_velocities
    //https://en.wikipedia.org/wiki/Velocity-addition_formula
    //Best way to think about the operation: take this and boost it by boostVel
    //Alternative definition
    /*def add(boostVel: Velocity): Velocity = {
      val dotProduct = this.dot(boostVel)
      def f(lhs: Double, rhs: Double): Double = {
        return (rhs + lhs / boostVel.gamma + dotProduct * rhs * boostVel.gamma / (1 + boostVel.gamma)) / (1 + dotProduct)
      }
      Velocity(f(this.x, boostVel.x), f(this.y, boostVel.y), f(this.z, boostVel.z))
      //Ideally, check that this velrss is less than 1 unless we are tacheoning
    }*/
    //Vector definition
    def add(boostVel: Velocity): Velocity = {
      val gamma = this.gamma
      val answer = this.v + boostVel.v / gamma +
        this.v * (this.v * boostVel.v) * (gamma / (1 + gamma))
      Velocity(answer / (1 + this.v * boostVel.v))
    }
    //def dot(vel: Velocity): Double = v * vel.v

    def direction: Position = Position(v.normalize)
    //def toarray: Array[Double] = val Array[this.x, this.y, this.z]
    def velrss: Double = this.v.length

    def gamma: Double = {
      1 / sqrt(1 - pow(velrss, 2))
    }
  }
  object Velocity{
    //This allows a call like the following without "new":
    //Velocity(x,y,z)
    def apply(xc: Double, yc: Double, zc: Double): Velocity = Velocity(Vector3d(xc, yc, zc))
  }

  val Origin = Position(0.0, 0.0, 0.0)
  val Stationary = Velocity(0.0, 0.0, 0.0)
  val Epoch = 0.0
  val LightSecond = 1.0
  val AU = 498.94 * LightSecond
  def Empty[T] = Set[T]()
}