package net.tedstein.rope

import net.tedstein.rope.Dimensions.{Position, Velocity}

import scala.math.{pow, sqrt}


/**
 * An actor in our little universe.
 */
class RelativisticObject(val pos: Position, val vel: Velocity, val time: Double) {
  def velrms: Double = {
    sqrt(List(vel.x, vel.y, vel.z).map(pow(_, 2)).sum / 3)
  }

  def gamma: Double = {
    1 / sqrt(1 - pow(velrms, 2))
  }
}