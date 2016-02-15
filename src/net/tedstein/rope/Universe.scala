package net.tedstein.rope

import net.tedstein.rope.physics.{Center, SimpleOrbiter, Orbiter, RelativisticObject, Dimensions}
import Dimensions.{Velocity, Position}
import net.tedstein.rope.physics.Vector3d

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
          if(math.random > 0.3)
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
          else
          new Orbiter(
            //All but primary and semiMajorAxisLength have a default, so commented out all lines that can use it
            primary = planet,
            semiMajorAxisLength = 0.15 + 0.1 * random.nextFloat(),
            eccentricity = 0.1 + 0.23 * random.nextFloat(),
            orbitalAxis = Position(Vector3d.randomDir + Vector3d(0,0,-1)), //bias it to -z dir, same as the simple ones
            initialPositionDirection = Dimensions.Origin, //why not, start pointing at the star
            //majorAxisSuggestion = Position(Vector3d.randomDir),
            //initialPos = Dimensions.Origin,
            //initialVel = Dimensions.Stationary,
            initialTime = Dimensions.Epoch,
            initialRadius = 0.01,
            //initialSatellites = Dimensions.Empty,
            mass = 0.01/5 //not that this is big enough to notice or affects anything
            )
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