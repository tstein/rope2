package net.tedstein.rope

import net.tedstein.rope.Dimensions.{Position, Velocity}

import scala.math.{pow, sqrt}


/**
 * An actor in our little universe.
 */
case class RelativisticObject(pos: Position, vel: Velocity, time: Double) {
  def velrms: Double = {
    sqrt(List(vel.x, vel.y, vel.z).map(pow(_, 2)).sum / 3)
  }

  def gamma: Double = {
    1 / sqrt(1 - pow(velrms, 2))
  }
}