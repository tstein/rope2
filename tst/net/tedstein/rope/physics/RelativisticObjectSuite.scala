package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
import net.tedstein.rope.physics.Dimensions.{Position, Velocity}


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
}
