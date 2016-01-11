package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
import net.tedstein.rope.physics.Dimensions.{Position, Velocity}


class RelativisticObjectSuite extends RopeSuite {
  test("stationary object") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(0, 0, 0), 0)
    assert(obj.velrss == 0.0)
    assert(obj.gamma == 1.0)
  }

  test("object moving in one dimension") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.1, 0, 0), 0)
    assertAlmostEquals(obj.velrss, .1, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.0050, epsilon(4))
  }

  test("object moving in three dimensions") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.3, .4, .5), 0)
    assertAlmostEquals(obj.velrss, .70711, epsilon(4))
    assertAlmostEquals(obj.gamma, 1.4142, epsilon(4))
  }

  test("object hauling ass") {
    val obj = new RelativisticObject(new Position(0, 0, 0), new Velocity(.52, .604, .6039), 0)  // magnitude 0.999956
    assertAlmostEquals(obj.velrss, 0.999956, epsilon(4))
    assertAlmostEquals(obj.gamma, 106.12507, epsilon(4))
  }
}