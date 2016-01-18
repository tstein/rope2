package net.tedstein.rope

import net.tedstein.rope.physics.{Center, SimpleOrbiter, RelativisticObject, Dimensions}
import Dimensions.{Velocity, Position}

import scala.collection.immutable.Stream.Empty


class Universe(val player: RelativisticObject, val bodies: Set[RelativisticObject])

object Universe {
  def demo: Universe = {
    val player = Center
    val bodies = (for (i <- 0 to 0) yield {
      val star = new SimpleOrbiter(Center,
        0.2,
        0.4,
        Dimensions.Origin.add(Position(.2, 0, 0)),
        Dimensions.Stationary,
        Dimensions.Epoch,
        Dimensions.Empty)
      val planets = for (i <- 0 to 0) yield {
        new SimpleOrbiter(star,
          0.7,
          0.3,
          star.pos.add(Position(.3, 0, 0)),
          Dimensions.Stationary,
          Dimensions.Epoch,
          Dimensions.Empty)
      }
      star.satellites = planets.toSet
      star.satellites + star
    }).flatten.toSet

    new Universe(player, bodies)
  }
}