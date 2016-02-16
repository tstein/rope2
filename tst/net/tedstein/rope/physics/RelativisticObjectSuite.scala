package net.tedstein.rope.physics

import net.tedstein.rope.{physics, RopeSuite}
import net.tedstein.rope.physics.Dimensions.{Position, Velocity}
import net.tedstein.rope.physics.Dimensions
import net.tedstein.rope.physics.{RelativisticObject, Orbiter, Center}


class RelativisticObjectSuite extends RopeSuite {
  test("stationary object") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(0, 0, 0), 0, 0, Dimensions.Empty)
    assert(obj.velrss == 0.0)
    assert(obj.gamma == 1.0)
  }

  test("object moving in one dimension") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.1, 0, 0), 0, 0, Dimensions.Empty)
    assertAlmostEquals(obj.velrss, .1, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.0050, epsilon(4))
  }

  test("object moving in three dimensions") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.3, .4, .5), 0, 0, Dimensions.Empty)
    assertAlmostEquals(obj.velrss, .70711, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.4142, epsilon(4))
  }

  test("object hauling ass") {
    // magnitude 0.999956
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.52, .604, .6039), 0, 0, Dimensions.Empty)
    assertAlmostEquals(obj.velrss, 0.999956, epsilon(4))
    assertAlmostEquals(obj.gamma, 106.12507, epsilon(4))
  }

  test("Eccentric Anomaly solver") {
    //Brute force: lets test the whole mapping
    def M(E: Double, ecc: Double): Double = E - ecc * math.sin(E)
    //Test an array of values
    var testE: Double = 0
    val testElimit: Double = 2 * math.Pi + 0.2
    var testEcc: Double = 0
    val testEccLimit: Double = 0.999
    var testM: Double = 0
    val tolerance: Double = 1E-6
    while(testE < testElimit){
      while(testEcc < testEccLimit){
        testM = M(testE, testEcc)
        assertAlmostEquals(testE, Orbiter.solveEccentricAnomaly(testM, testEcc), tolerance)
        testEcc = testEcc + testEccLimit / 32 - 1E-6
      }
      testE = testE + testElimit / 32 - 1E-6
    }
    testE = testElimit
    testEcc = testEccLimit
    testM = M(testE, testEcc)
    assertAlmostEquals(testE, Orbiter.solveEccentricAnomaly(testM, testEcc), tolerance)
  }

  test("True Anomaly solver") {
    //Check that it is strictly ascending for 2 periods modulo 2pi
    val testEinit: Double = -math.Pi //Yea, start right on the asymptote
    val testElimit: Double = math.Pi * 3
    val testEccList = List[Double](0.0, 0.1, 0.8, 0.9, 0.999)
    val tolerance: Double = 1E-9
    for(testEcc <- testEccList) {
      var testE: Double = testEinit
      var lastTheta = Orbiter.solveTrueAnomaly(testE, testEcc)
      var thisTheta = lastTheta
      while(testE < testElimit + 1E-4){
        lastTheta = thisTheta
        thisTheta = Orbiter.solveTrueAnomaly(testE, testEcc)
        //And check these are increasing
        val increaseTheta = ((thisTheta - lastTheta) + 4 * math.Pi + tolerance) % (2 * math.Pi)
        if(testEcc <= 0.9)
          assert( increaseTheta < 1.5 )
        else
          assert( increaseTheta < 4 ) //Swings around quickly in these cases
        assert( increaseTheta >= 0.0 )
        testE = testE + (testElimit - testEinit) / 64
      }
    }
  }

  test("Slow and steady orbiter: Earth around sun") {
    var time: Double = 0
    val sun: RelativisticObject = new RelativisticObject(
      initialPos = Dimensions.Origin,
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = 4.643,
      initialSatellites = Dimensions.Empty,
      mass = Orbiter.mass.sun)
    val earth: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 1.0 * Dimensions.AU, //499
      eccentricity = 0.017,
      orbitalAxis = Position(0,0,15),
      initialPositionDirection = Position(35,0,0),
      majorAxisSuggestion = Position(-0.15,0,0),
      initialTime = time,
      initialRadius = 0.021,
      mass = Orbiter.mass.earth
    )
    //Okay, now check that these things match what real life has
    assertAlmostEquals(earth.orbitalPeriod / (365.24 * 24 * 60 * 60), 1, 3E-2)
    assertAlmostEquals(math.sin(earth.theta), 0, 1E-5)
    assertAlmostEquals(earth.velocityToPrimary.velrss / (2.979E4 / 3E8), 1, 2E-2)
    assertAlmostEquals(earth.offsetToPrimary.v.length / 491, 1, 5E-3)

    //Wait half a year, and check apoapsis
    time += 0.5 * 365.24 * 24 * 60 * 60
    earth.updatePositionAndVelocity(time)

    assertAlmostEquals(math.cos(earth.theta), -1, 1E-3)
    assertAlmostEquals(math.sin(earth.theta), 0, 2E-2)
    assertAlmostEquals(earth.velocityToPrimary.velrss / (2.979E4 / 3E8), 1, 2E-2)
    assertAlmostEquals(earth.offsetToPrimary.v.length / 507, 1, 5E-3)

    //Wait another half year, check periapsis again
    time += 0.5 * 365.24 * 24 * 60 * 60
    earth.updatePositionAndVelocity(time)

    assertAlmostEquals(math.sin(earth.theta), 0, 3E-2)
    assertAlmostEquals(earth.velocityToPrimary.velrss / (2.979E4 / 3E8), 1, 2E-2)
    assertAlmostEquals(earth.offsetToPrimary.v.length / 491, 1, 5E-3)

  }

  test("Slow and elliptical orbiter: Halley's comet around sun") {
    var time: Double = 0
    val sun: RelativisticObject = new RelativisticObject(
      initialPos = Dimensions.Origin,
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = 4.643,
      initialSatellites = Dimensions.Empty,
      mass = Orbiter.mass.sun)
    //https://en.wikipedia.org/wiki/Halley%27s_Comet
    val halleysComet: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 17.8 * Dimensions.AU,
      eccentricity = 0.967,
      orbitalAxis = Position(0,0,1),
      initialPositionDirection = Position(1,0,0),
      majorAxisSuggestion = Position(-1,0,0),
      initialTime = time,
      initialRadius = 11 * Dimensions.kilometer,
      mass = 2.2E14 * Orbiter.mass.kiloGram
    )
    //Okay, now check that these things match what real life has
    assertAlmostEquals(halleysComet.orbitalPeriod / (75.3 * 365.24 * 24 * 60 * 60), 1, 9E-2)
    assertAlmostEquals(halleysComet.offsetToPrimary.v.length / (0.586 * Dimensions.AU), 1, 1E-2)

    //Wait a half orbit
    time += halleysComet.orbitalPeriod/2
    halleysComet.updatePositionAndVelocity(time)

    assertAlmostEquals(halleysComet.offsetToPrimary.v.length / (35.1 * Dimensions.AU), 1, 1E-2)

    //Wait till what should be a 1 AU crossover (~80 day window between them)
    time += halleysComet.orbitalPeriod * 0.5 - (40 * 24 * 60 * 60)
    halleysComet.updatePositionAndVelocity(time)

    //Direction should be significantly towards the sun
    assert(-0.3 >
      (halleysComet.velocityToPrimary.v * halleysComet.offsetToPrimary.v) /
        (halleysComet.velocityToPrimary.velrss * halleysComet.offsetToPrimary.v.length)
    )
    //Distance should be within 0.85 to 1.5 AU
    assert(halleysComet.offsetToPrimary.v.length < 1.5  * Dimensions.AU)
    assert(halleysComet.offsetToPrimary.v.length > 0.85 * Dimensions.AU)

  }

  test("Orbiter: Conservation of specific orbital energy (VERY MUCH NEWTONIAN) and integration sanity") {
    val sun: RelativisticObject = new RelativisticObject(
      initialPos = Position(0,0,0),
      initialVel = Velocity(0,0,0),
      initialTime = Dimensions.Epoch,
      initialRadius = 4.643,
      initialSatellites = Dimensions.Empty,
      mass = Orbiter.mass.sun)
    val earth: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 1.0 * Dimensions.AU, //499
      eccentricity = 0.017,
      orbitalAxis = Position(0,0,15),
      initialPositionDirection = Position(35,0,0),
      majorAxisSuggestion = Position(-0.15,0,0),
      initialTime = 0,
      initialRadius = 0.021,
      mass = Orbiter.mass.earth
    )
    val eccentricEarth: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 1.0 * Dimensions.AU, //499
      eccentricity = 0.417,
      orbitalAxis = Position(0,0,15),
      initialPositionDirection = Position(35,0,0),
      majorAxisSuggestion = Position(-0.15,0,0),
      initialTime = 0,
      initialRadius = 0.021,
      mass = Orbiter.mass.earth
    )
    //https://en.wikipedia.org/wiki/Halley%27s_Comet
    val halleysComet: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 17.8 * Dimensions.AU,
      eccentricity = 0.967,
      orbitalAxis = Position(0,0,1),
      initialPositionDirection = Position(1,0,0),
      majorAxisSuggestion = Position(-1,0,0),
      initialTime = 0,
      initialRadius = 11 * Dimensions.kilometer,
      mass = 2.2E14 * Orbiter.mass.kiloGram
    )
    val halleysCometRandDir: Orbiter = new Orbiter(
      primary = sun,
      semiMajorAxisLengthSuggestion = 17.8 * Dimensions.AU,
      eccentricity = 0.967,
      initialTime = 0
    )
    val defaulter: Orbiter = new Orbiter(
      primary = sun
    )
    def tester(subject: Orbiter) = {
      val mu: Double = sun.mass * 0.5
      val specificOrbitalEnergy: Double = - mu / (2 * subject.semiMajorAxisLength)
      var time: Double = 0
      subject.updatePositionAndVelocity(time)
      var lastPosition = subject.offsetToPrimary
      var lastVelocity = subject.velocityToPrimary
      val resolution: Double = 1.0/128 //Will take about 2 * PI * 1/this steps to complete a revolution
      while(time < subject.orbitalPeriod * 1.1){
        lastPosition = subject.offsetToPrimary
        lastVelocity = subject.velocityToPrimary
        val timeToCenterIfAimedThatWay: Double = lastPosition.v.length / lastVelocity.velrss
        val timeStep = timeToCenterIfAimedThatWay * resolution
        time += timeStep
        subject.updatePositionAndVelocity(time)
        val avgVelocity = Velocity((lastVelocity.v + subject.velocityToPrimary.v) * 0.5)
        val tolerance = lastPosition.v.length * resolution * 0.1
        val impliedVelocity = Velocity((subject.offsetToPrimary.v - lastPosition.v) / timeStep)

        //Check: does the velocity follow the position path?
        assertAlmostEquals(subject.offsetToPrimary,
                          lastPosition.drift(avgVelocity, timeStep),
                          tolerance)
        assertAlmostEquals(impliedVelocity, avgVelocity, avgVelocity.velrss * resolution * 0.1)

        //Check: does energy add up?
        //https://en.wikipedia.org/wiki/Elliptic_orbit#Energy
        //TODO: Only valid for Newtonian!
        assertAlmostEquals(
          (math.pow(subject.velocityToPrimary.velrss, 2) * 0.5 -  mu / subject.offsetToPrimary.v.length) /
                                           specificOrbitalEnergy,
          1,
          resolution * 0.1
        )
      }
    }
    tester(earth)
    tester(eccentricEarth)
    tester(halleysComet)
    tester(halleysCometRandDir)
    tester(defaulter)
  }

  test("Orbiter: Neat constructor things that should be legal") {
    var time: Double = 0
    val blackHole: RelativisticObject = new RelativisticObject(
      initialPos = Dimensions.Origin,
      initialVel = Dimensions.Stationary,
      initialTime = Dimensions.Epoch,
      initialRadius = 1,
      initialSatellites = Dimensions.Empty,
      mass = 1)
    val defaulter: Orbiter = new Orbiter(
      primary = blackHole
    )
    val diver: Orbiter = new Orbiter(
      primary = blackHole,
      //semiMajorAxisLengthSuggestion = 1.0 * Dimensions.AU, //499
      eccentricity = 0.999,
      orbitalAxis = Position(0,0,15),
      initialPositionDirection = Position(35,0,0),
      majorAxisSuggestion = Position(-0.15,0,0),
      initialTime = time
      //initialRadius = 0.021,
      //mass = Orbiter.mass.earth
    )
    assert(defaulter.semiMajorAxisLength > 0)
    assert(diver.semiMajorAxisLength > 0)
    assert(diver.velocityToPrimary.velrss > 0.5)
  }
}
