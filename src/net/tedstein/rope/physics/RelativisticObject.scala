package net.tedstein.rope.physics

import net.tedstein.rope.physics.Dimensions.{Position, Velocity}

import scala.math.{pow, sqrt}


/**
 * An actor in our little universe.
 */
case class RelativisticObject(var pos: Position, var vel: Velocity, var time: Double) {
  def velrss: Double = {
    sqrt(List(vel.x, vel.y, vel.z).map(pow(_, 2)).sum)
  }

  def gamma: Double = {
    1 / sqrt(1 - pow(velrss, 2))
  }
}