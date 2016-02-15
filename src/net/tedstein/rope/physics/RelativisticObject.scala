package net.tedstein.rope.physics

import net.tedstein.rope.physics.Dimensions.{Position, Velocity}
import net.tedstein.rope.physics.Vector3d
import scala.math.{pow, sqrt}


/**
  * An actor in our little universe.
  *
  * @param initialSatellites: Other <code>RelativisticObjects</code> orbiting this one.
  */
sealed class RelativisticObject(private val initialPos: Position,
                                private val initialVel: Velocity,
                                private val initialTime: Double,
                                private val initialRadius: Double,
                                private val initialSatellites: Set[RelativisticObject],
                                //Mass, in units of light seconds as Schwarzschild Radius
                                val mass: Double = Orbiter.mass.kiloGram) {
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
  Dimensions.Empty,
  Orbiter.mass.kiloGram
)


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

/**
  * A <code>RelativisticObject</code> that is in a Newtonian elliptical orbit around
  * another <code>RelativisticObject</code>.
  *
  * @param primary The object that this <code>RelativisticObject</code> is orbiting.
  * @param semiMajorAxisLength The "radius" of the elliptical orbit in its longer direction
  * @param eccentricity How round vs elongated the orbit is.
  *                     0 == circle, [0,1) == ellipse, 1 == parabola, (1,inf) = hyperbola
  *                     Function will break if given something outside of [0,1), and probably
  *                     behave poorly for values close to 1.  Refuses to operate past e=0.999
  * @param orbitalAxis Axis of the orbit.  Right hand rule applies to orbit direction.
  * @param mass Mass of the object, expressed as Schwarzschild radii in light seconds.  Look at
  *              some objects in Orbiter.Mass??? for constants.  (C = constructor)
  * @param initialPositionDirection Direction from the primary that the object starts at.  Will
  *                                 auto-fix any length or alignment issues to the axis it has.
  * @param majorAxisSuggestion When aligned perpendicular to the orbitalAxis (hence suggestion)
  *                            this becomes the major (longer) axis direction
  */
class Orbiter(primary: RelativisticObject,
              val semiMajorAxisLength: Double,
              val eccentricity: Double = math.random * 0.8,
              orbitalAxis: Position = Position(Vector3d.randomDir),
              initialPositionDirection: Position = Position(Vector3d.randomDir),
              majorAxisSuggestion: Position = Position(Vector3d.randomDir),
              private val initialPos: Position = Dimensions.Origin,
              private val initialVel: Velocity = Dimensions.Stationary,
              private val initialTime: Double = 100 + math.random * 10,
              private val initialRadius: Double = 0.021, //Earth-ish as a default
              private val initialSatellites: Set[RelativisticObject] = Dimensions.Empty,
              mass: Double = Orbiter.mass.kiloGram //Small default
              ) extends RelativisticObject(initialPos, initialVel, initialTime, initialRadius, initialSatellites, mass) {

  //Finish constructing this
  //Setup our "coordinate axes"
  val orbitalAxisDir: Vector3d = orbitalAxis.v.normalize
  private val majorAxisDir: Vector3d = {
    majorAxisSuggestion.v - orbitalAxisDir * (majorAxisSuggestion.v * orbitalAxisDir)
  }
  private val minorAxisDir: Vector3d = orbitalAxisDir.cross(majorAxisDir)
  //Sanity check this setup (debug only)
  //TODO: something less jarring than assertions failing
  assert(math.abs(minorAxisDir.length - 1) < 1E-4)

  //Sanity check eccentricity
  assert(eccentricity < 0.999, "Bad eccentricity value of " + eccentricity + " given")
  assert(eccentricity >= 0, "Bad eccentricity value of " + eccentricity + " given")

  //Sanity check that the orbit is valid
  assert(semiMajorAxisLength > 0, "Don't give a negative semiMajorAxisLength, you gave " + semiMajorAxisLength)
  private val periapsisLength: Double = semiMajorAxisLength * (1 - eccentricity)
  private val apoapsisLength: Double = semiMajorAxisLength * (1 + eccentricity)
  assert(periapsisLength > primary.mass * 3,
    "This object will fall into its primary, its (periapsis " + periapsisLength +
    " is less than 3 * Schwarzschild radius of its parent (3 * " + primary.mass + ")")
  //3r is the innermost stable orbit, use that for now

  //Primary body's GM constant (units: speed of light per second, acceleration)
  //Also represented as mu, or the standard gravitational parameter
  private val primaryGM: Double = primary.mass / 2
  assert(primaryGM>0, "No negative masses!")

  //Specific angular momentum (also l/m)
  private val specificAngularMomentum: Double =
    math.sqrt(periapsisLength * primaryGM * (1 + eccentricity))

  //Orbital period
  val orbitalPeriod: Double = 2 * math.Pi * math.sqrt(math.pow(semiMajorAxisLength, 3) / primaryGM)

  //Semi-latus rectum, also as l https://en.wikipedia.org/wiki/Conic_section#Conic_parameters
  private val semiLatusRectum: Double = semiMajorAxisLength * (1 - eccentricity * eccentricity)

  //Initial mean anomaly (M)
  //Ideally, true anomaly would be set by this rather than mean.
  private val initMeanAnomaly = math.atan2(
    -(minorAxisDir * initialPositionDirection.v),
    -(majorAxisDir * initialPositionDirection.v)
  )

  //Initial position/velocity for the orbit
  var theta: Double = 0
  var offsetToPrimary: Position = Position(0,0,0)
  var velocityToPrimary: Velocity = Velocity(0,0,0)
  updatePositionAndVelocity(time)

  //https://en.wikipedia.org/wiki/Kepler%27s_laws_of_planetary_motion#Position_as_a_function_of_time
  //https://en.wikipedia.org/wiki/Orbit_equation
  //https://en.wikipedia.org/wiki/Elliptic_orbit#Velocity
  //Compute position and velocity as a function of only time
  def updatePositionAndVelocity(updateTime: Double) = {
    time = updateTime

    //Compute mean anomaly (M) (angle of primary - this - fake circle orbit), 0 = periapsis
    val meanAnomaly: Double = initMeanAnomaly + 2 * math.Pi * time / orbitalPeriod

    //Compute eccentric anomaly (E) (solve M = E - ecc * sin(E) )
    val eccentricAnomaly: Double = Orbiter.solveEccentricAnomaly(meanAnomaly, eccentricity)

    //Compute true anomaly (theta)
    theta = Orbiter.solveTrueAnomaly(eccentricAnomaly, eccentricity)

    //Compute the relative position (r = a * (1-ecc^2) / (1-ecc*cos(theta), project to coords)
    val distToPrimary = semiLatusRectum * (1 - eccentricity * math.cos(theta))
    //Okay, so theta==0 is pointing to periapsis and is off by Pi
    offsetToPrimary = Position(majorAxisDir * (math.cos(theta + math.Pi) * distToPrimary) +
                               minorAxisDir * (math.sin(theta + math.Pi) * distToPrimary) )

    //Compute the relative velocity
    //TODO: adjust for relativity
    //Speed perpendicular to the direction to the primary, based on conservation of angular momentum
    val speedPerpToPrimary = specificAngularMomentum / distToPrimary
    //And the radial component
    val radialSpeedToPrimary = speedPerpToPrimary *
      math.atan(-eccentricity * math.sin(theta) /
        (1 - eccentricity * math.cos(theta))
      )
    //And plop it into the right coordinates
    val radialDirection: Vector3d = offsetToPrimary.v.normalize
    val progradeDirection: Vector3d = orbitalAxisDir.cross(radialDirection)
    velocityToPrimary = Velocity(progradeDirection * speedPerpToPrimary + radialDirection * radialSpeedToPrimary)

    assert(velocityToPrimary.velrss < 1, "Found invalid orbital speed!")

    //Update for absolute position and velocity
    pos = primary.pos.add(offsetToPrimary)
    vel = velocityToPrimary.add(primary.vel)
  }

  //Just call pos and vel?
  //def computedPosition: Position = pos
  //def computedVelocity: Velocity = vel
}
object Orbiter{
  //Okay, lets get some mass conversions down
  //all assumes light * second == 1
  object mass {
    val sun: Double = 9.8E-6
    val jupiter: Double = 7.34E-9
    val earth: Double = 8.87E-11
    val moon: Double = 3.67E-13
    val kiloGram: Double = 4.94E-36
    //May want to think about this in terms of interesting orbital periods
    //Near black holes: http://casa.colorado.edu/~ajsh/orbit.html
    //(tl;dr: 8000*sun is a second, and proportional to time for the r=2Rs case)
  }
  def solveEccentricAnomaly(meanAnomaly: Double, eccentricity: Double): Double = {
    //Compute eccentric anomaly (solve for E, given M & ecc:  M = E - ecc * sin(E) )
    //Don't feed me bullshit
    assert(eccentricity <= 0.999, "Bad eccentricity value of " + eccentricity + " given")
    assert(eccentricity >= -0.5, "Bad eccentricity value of " + eccentricity + " given")
    var convergence = 0.85
    var iterations = 12
    if(eccentricity > 0.97){ //numerical solver corner case
      //if we want much farther, need to make a new solver algorithm
      convergence = 0.6
      iterations = 20
    }
    var guess: Double = meanAnomaly
    var error: Double = 0
    for(i <- 1 to iterations){
      //Newton's method, slowed and stabilized by convergence
      error = - (guess - eccentricity * math.sin(guess) - meanAnomaly)
      guess = guess + error * convergence / (1 - eccentricity * math.cos(guess))

      //If error is more than a full circle, something went wrong
      assert(math.abs(error) < 7,
        "Convergence failed at eccentricity = " + eccentricity +
        ", meanAnomaly = " + meanAnomaly +
        ", iteration = " + i)
    }
    guess
  }
  def solveTrueAnomaly(eccentricAnomaly: Double, eccentricity: Double): Double = {
    assert(eccentricity > -0.99)
    assert(eccentricity <= 0.999)

    //Solve for theta: a * cos(E) = a * ecc + r * cos(theta), equivalently
    //cos(E) = (ecc + cos(theta)) / (1 + ecc * cos(theta)), equivalently
    //(1 - ecc) * tan(theta / 2) ^ 2 = (1 + ecc) * tan(E/2) ^ 2
    val eTan: Double = math.tan(eccentricAnomaly / 2)
    val tTan: Double = eTan * sqrt((1 + eccentricity) / (1 - eccentricity))
    var answer: Double = 2 * math.atan(tTan)
    if(answer.isNaN)       //Assume we hit an asymptote
      answer = math.Pi     //the only spot that would solve that
    answer
  }
}
