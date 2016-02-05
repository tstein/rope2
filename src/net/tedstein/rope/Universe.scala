package net.tedstein.rope

import net.tedstein.rope.physics.{Center, SimpleOrbiter, RelativisticObject, Dimensions}
import Dimensions.{Velocity, Position}

import scala.collection.immutable.Stream.Empty
import scala.util.Random


class Universe(val player: RelativisticObject, val bodies: Set[RelativisticObject])

object Universe {
  def demo: Universe = {
    val random = new Random()
    val player = Center
    val bodies = (for (_ <- 0 to 0) yield {
      val star = new SimpleOrbiter(Center,
        0.0,
        0.6,
        Dimensions.Origin.add(Position(0, 0, 0)),
        Dimensions.Stationary,
        Dimensions.Epoch + 100 * random.nextFloat(),
        0.15,
        Dimensions.Empty)
      val planets = for (_ <- 0 to 3) yield {
        val planet = new SimpleOrbiter(star,
          0.7 + .7 * random.nextFloat(),
          0.5 + .1 * random.nextFloat(),
          star.pos.add(Position(.3, 0, 0)),
          Dimensions.Stationary,
          Dimensions.Epoch + 100 * random.nextFloat(),
          0.07,
          Dimensions.Empty)
        val moons = for (_ <- 0 to 2) yield {
          new SimpleOrbiter(planet,
            0.1 + .2 * random.nextFloat(),
            0.3 + .1 * random.nextFloat(),
            planet.pos.add(Position(.1, 0, 0)),
            Dimensions.Stationary,
            Dimensions.Epoch + 100 * random.nextFloat(),
            0.01,
            Dimensions.Empty)
        }
        planet.satellites = moons.toSet
        planet
      }
      star.satellites = planets.toSet
      star.satellites ++ planets.flatMap(_.satellites) + star
    }).flatten.toSet

    new Universe(player, bodies)
  }
}