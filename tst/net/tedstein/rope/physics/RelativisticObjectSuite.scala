package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
import net.tedstein.rope.physics.Dimensions.{Position, Velocity}


class RelativisticObjectSuite extends RopeSuite {
  test("stationary object") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(0, 0, 0), 0)
    assert(obj.velrms == 0.0)
    assert(obj.gamma == 1.0)
  }

  test("object moving in one dimension") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.1, 0, 0), 0)
    assertAlmostEquals(obj.velrms, .0577, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.0017, epsilon(4))
  }

  test("object moving in three dimensions") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.3, .4, .5), 0)
    assertAlmostEquals(obj.velrms, .4082, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.0954, epsilon(4))
  }

  test("object hauling ass") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.52, .604, .6039), 0)  // magnitude 0.999956
    assertAlmostEquals(obj.velrms, .5773, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.2247, epsilon(4))
  }
}