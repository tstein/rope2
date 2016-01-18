package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
import net.tedstein.rope.physics.Dimensions._


class DimensionsSuite extends RopeSuite {
  test("zero velocity addition") {
    val v1 = new Velocity(0, 0, 0)
    val v2 = new Velocity(0, 0, 0)
    assert(v1.add(v2) == Dimensions.Stationary)
    assert(v1 == v2)
    assert(v1.add(v2).velrss < 1)
  }

  test("velocity addition does not exceed c") {
    val v1 = new Velocity(.9, 0, 0)
    val v2 = new Velocity(.999, 0, 0)
    assert(v1.add(v2).velrss < 1)
  }

  test("velocity addition commutability 1D parallel") {
    val v1 = new Velocity(.4, 0, 0)
    val v2 = new Velocity(.7, 0, 0)
    assertAlmostEquals(v1.add(v2).velrss, v2.add(v1).velrss, epsilon(4))
    assert(v1.add(v2).velrss < 1)
  }

  //Note: it is not 100% commutative.  Direction will not commute.
  //https://en.wikipedia.org/wiki/Thomas_precession
  /*test("velocity addition commutability 3D") {
    val v1 = new Velocity(.1, 0, 0)
    val v2 = new Velocity(0, 0, 0)
    //TODO: upgrade assertAlmostEquals for cases like this
    //assertAlmostEquals(v1.add(v2), v2.add(v1), epsilon
    assert(v1.add(v2).velrss < 1)
  }*/

  test("velocity addition in one dimension") {
    //Should match:
    //s = (u+v)/(1+uv/c2)
    //https://en.wikipedia.org/wiki/Velocity-addition_formula#Special_relativity
    val u = .4
    val v = .6
    val v1 = new Velocity(u, 0, 0)
    val v2 = new Velocity(v, 0, 0)
    assertAlmostEquals(v1.add(v2).velrss, (v+u)/(1+u*v), epsilon(4))
    assert(v1.add(v2).velrss < 1)
  }

  /*test("velocity addition in three dimensions, colinear") {
    val v1 = new Velocity(.3, .4, .5) //mag = .70711
    val v2 = new Velocity(.3, .4, .5) //mag = .70711
    assert(v1.add(v2).velrss < 1)
  }

  test("velocity addition in three dimensions, general") {
    val v1 = new Velocity(.3, .4, .5) //mag = .70711
    val v2 = new Velocity(.3, .4, .5) //mag = .70711
    assert(v1.add(v2).velrss < 1)
  }

  test("velocity addition hauling ass") {
    val v1 = new Velocity(.52, .604, .6039)  // magnitude 0.999956
    assert(v1.add(v2).velrss < 1)
  }*/
}