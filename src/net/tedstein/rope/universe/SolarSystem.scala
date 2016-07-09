package net.tedstein.rope.universe

import net.tedstein.rope.graphics.{Mesh, Texture}
import net.tedstein.rope.physics.{Center, Dimensions, RelativisticObject, SimpleOrbiter}
import net.tedstein.rope.physics.Dimensions.Position

import scala.util.Random

object SolarSystem {
  private val random = new Random()

  def create: Universe = create(distanceScale = 1.0 / 200, orbitalSpeedScale = 100000)

  def create(distanceScale: Double, orbitalSpeedScale: Double): Universe = {
    val player = new RelativisticObject(
      Position(0, 0, 15),
      Dimensions.Stationary,
      Dimensions.Epoch,
      Dimensions.LightSecond,
      Dimensions.Empty,
      0
    )

    val sun = new SimpleOrbiter(
      primary = Center,
      orbitalDistance = 0.0,
      angularFrequency = 0.6,
      initialPos = Dimensions.Origin,
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch + 100 * random.nextFloat(),
      initialRadius = .232,
      initialSatellites = Dimensions.Empty,
      mass = 0.5)
    sun.texture = Texture.Sun
    sun.mesh = Mesh.Sphere

    val mercury = new SimpleOrbiter(
      primary = sun,
      orbitalDistance = 160.1 * distanceScale,
      angularFrequency = 8.267e-7 * orbitalSpeedScale,
      initialPos = sun.pos.add(Position(0, 0, 0)),
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = .00814,
      initialSatellites = Dimensions.Empty)
    mercury.texture = Texture.Moon
    mercury.mesh = Mesh.Sphere

    val venus = new SimpleOrbiter(
      primary = sun,
      orbitalDistance = 358.5 * distanceScale,
      angularFrequency = 3.236e-7 * orbitalSpeedScale,
      initialPos = sun.pos.add(Position(0, 0, 0)),
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = .0202,
      initialSatellites = Dimensions.Empty)
    venus.texture = Texture.Moon
    venus.mesh = Mesh.Sphere

    val earth = new SimpleOrbiter(
      primary = sun,
      orbitalDistance = 507.3 * distanceScale,
      angularFrequency = 1.991e-7 * orbitalSpeedScale,
      initialPos = sun.pos.add(Position(0, 0, 0)),
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = .0212,
      initialSatellites = Dimensions.Empty)
    earth.texture = Texture.Earth
    earth.mesh = Mesh.Sphere

    val moon = new SimpleOrbiter(
      primary = earth,
      orbitalDistance = 1.317 * distanceScale,
      angularFrequency = 2.671e-6 * orbitalSpeedScale,
      initialPos = earth.pos.add(Position(0, 0, 0)),
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = .0058,
      initialSatellites = Dimensions.Empty)
    moon.texture = Texture.Moon
    moon.mesh = Mesh.Sphere

    earth.satellites = Set(moon)

    val mars = new SimpleOrbiter(
      primary = sun,
      orbitalDistance = 728.8 * distanceScale,
      angularFrequency = 1.056e-7 * orbitalSpeedScale,
      initialPos = sun.pos.add(Position(0, 0, 0)),
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = .0113,
      initialSatellites = Dimensions.Empty)
    mars.texture = Texture.Mars
    mars.mesh = Mesh.Sphere

    sun.satellites = Set(mercury, venus, earth, mars)

    val bodies = Set[RelativisticObject](sun, mercury, venus, earth, moon, mars)
    new Universe(player, bodies)
  }
}
