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
      val star = new SimpleOrbiter(
        primary = Center,
        orbitalDistance = 0.0,
        angularFrequency = 0.6,
        initialPos = Dimensions.Origin.add(Position(0, 0, 0)),
        initialVel = Dimensions.Stationary,
        initialTime = Dimensions.Epoch + 100 * random.nextFloat(),
        initialRadius = 0.15,
        initialSatellites = Dimensions.Empty,
        mass = 0.03) // Sticking to radius / 5 for now (excitingly dense)
      val planets = for (_ <- 0 to 3) yield {
        val planet = new SimpleOrbiter(
          primary = star,
          orbitalDistance = 0.7 + .7 * random.nextFloat(),
          angularFrequency = 0.5 + .1 * random.nextFloat(),
          initialPos = star.pos.add(Position(.3, 0, 0)),
          initialVel = Dimensions.Stationary,
          initialTime = Dimensions.Epoch + 100 * random.nextFloat(),
          initialRadius = 0.07,
          initialSatellites = Dimensions.Empty,
          mass = 0.07/5)
        val moons = for (_ <- 0 to 2) yield {
          new SimpleOrbiter(
            primary = planet,
            orbitalDistance = 0.1 + .2 * random.nextFloat(),
            angularFrequency = 0.3 + .1 * random.nextFloat(),
            initialPos = planet.pos.add(Position(.1, 0, 0)),
            initialVel = Dimensions.Stationary,
            initialTime = Dimensions.Epoch + 100 * random.nextFloat(),
            initialRadius = 0.01,
            initialSatellites = Dimensions.Empty,
            mass = 0.01/5)
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