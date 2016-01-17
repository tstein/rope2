package net.tedstein.rope.physics

import net.tedstein.rope.physics.Dimensions.{Position, Velocity}

import scala.math.{pow, sqrt}


/**
 * An actor in our little universe.
 */
case class RelativisticObject(var pos: Position, var vel: Velocity, var time: Double) {
  def velrss: Double = vel.velrss
  def gamma: Double = vel.gamma
}