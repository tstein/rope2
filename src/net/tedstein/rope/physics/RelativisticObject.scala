package net.tedstein.rope.physics

import net.tedstein.rope.physics.Dimensions.{Position, Velocity}

import scala.math.{pow, sqrt}


/**
  * An actor in our little universe.
  *
  * @param satellites: Other <code>RelativisticObjects</code> orbiting this
  *                  one.
  */
sealed class RelativisticObject(private val initialPos: Position,
                                private val initialVel: Velocity,
                                private val initialTime: Double,
                                private val initialRadius: Double,
                                private val initialSatellites: Set[RelativisticObject]) {
  var pos = initialPos
  var vel = initialVel
  var time = initialTime
  val radius = initialRadius
  var satellites = initialSatellites

  def velrss: Double = vel.velrss
  def gamma: Double = vel.gamma
}


/**
  * The center of the Universe. Probably, like, a black hole.
  */
object Center extends RelativisticObject(Dimensions.Origin,
  Dimensions.Stationary,
  Dimensions.Epoch,
  Dimensions.LightSecond,
  Dimensions.Empty)


/**
  * A <code>RelativisticObject</code> that is in a circular orbit around
  * another <code>RelativisticObject</code> in the "ground" plane.
  *
  * @param primary The object that this <code>RelativisticObject</code> is orbiting.
  * @param orbitalDistance The distance between the gravitational center of
  *                        this <code>RelativisticObject</code> and the
  *                        gravitational center of its <code>primary</code>.
  * @param angularFrequency In radians/second.
  */
class SimpleOrbiter(primary: RelativisticObject,
                    orbitalDistance: Double,
                    angularFrequency: Double,
                    private val initialPos: Position,
                    private val initialVel: Velocity,
                    private val initialTime: Double,
                    private val initialRadius: Double,
                    private val initialSatellites: Set[RelativisticObject]
                   ) extends RelativisticObject(initialPos, initialVel, initialTime, initialRadius, initialSatellites) {
  def cosineOrbit(phaseOffset: Double, center: Double, amplitude: Double): Double = {
    center + amplitude * math.cos(phaseOffset)
  }

  def computedPosition: Position = Position(
      cosineOrbit(time * angularFrequency, primary.pos.x, orbitalDistance),
      cosineOrbit(math.Pi / 2 + time * angularFrequency, primary.pos.y, orbitalDistance),
      cosineOrbit(math.Pi / 2 + time * angularFrequency, primary.pos.z, 0))

  def computedVelocity: Velocity = Velocity(
    cosineOrbit(-math.Pi / 2 + time * angularFrequency, primary.vel.x, orbitalDistance * angularFrequency),
    cosineOrbit(time * angularFrequency, primary.vel.y, orbitalDistance * angularFrequency),
    cosineOrbit(math.Pi / 2 + time * angularFrequency, primary.vel.z, 0))
}