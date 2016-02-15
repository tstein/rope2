package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
import net.tedstein.rope.physics.Dimensions._


class DimensionsSuite extends RopeSuite {
  test("Velocity constructors") {
    val v1 = new Velocity(.2, .2, .2)
    val v2 = Velocity(.2, .2, .2)
    val v3 = Velocity(Vector3d(.2, .2, .2))
    val v4 = Velocity(new Vector3d(.2, .2, .2))
    val v5 = new Velocity(Vector3d(.2, .2, .2))
    val v6 = new Velocity(new Vector3d(.2, .2, .2))
    assert(v1 == v2)
    assert(v1 == v3)
    assert(v1 == v4)
    assert(v1 == v5)
    assert(v1 == v6)
    assert(v2 == v6)
  }

  test("Velocity get xyz methods"){
    val v1 = Velocity(.1,.2,.3)
    assert(v1.x == 0.1)
    assert(v1.y == 0.2)
    assert(v1.z == 0.3)
  }

  test("Velocity get v method"){
    val v1 = Velocity(.1,.2,.3)
    assert(v1.v == Vector3d(.1, .2, .3))
  }
  //Note: members of case classes are val, not var

  test("zero velocity addition") {
    val v1 = new Velocity(0, 0, 0)
    val v2 = new Velocity(0, 0, 0)
    assert(v1.boost(v2) == Dimensions.Stationary)
    assert(v1 == v2)
    assert(v1.boost(v2).velrss < 1)
  }

  test("velocity addition does not exceed c") {
    val v1 = new Velocity(.9, 0, 0)
    val v2 = new Velocity(.999, 0, 0)
    assert(v1.boost(v2).velrss < 1)
  }

  test("velocity addition commutability 1D parallel") {
    val v1 = new Velocity(.4, 0, 0)
    val v2 = new Velocity(.7, 0, 0)
    assertAlmostEquals(v1.boost(v2).velrss, v2.boost(v1).velrss, epsilon(4))
    assert(v1.boost(v2).velrss < 1)
  }

  //Note: it is not 100% commutative.  Direction will not commute.
  //https://en.wikipedia.org/wiki/Thomas_precession
  /*test("velocity addition commutability 3D") {
    val v1 = new Velocity(.1, 0, 0)
    val v2 = new Velocity(0, 0, 0)
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
    assertAlmostEquals(v1.boost(v2).velrss, (v+u)/(1+u*v), epsilon(4))
    assert(v1.boost(v2).velrss < 1)
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
  test("Position.Dotproduct") {
    //Just some random dot products
    assertAlmostEquals(Position(1,5,2).dot(Position(1,3,5)), 1+15+10, epsilon(4))
    assertAlmostEquals(Position(1,-5,2).dot(Position(1,3,5)), 1-15+10, epsilon(4))
    assertAlmostEquals(Position(1,5,2).dot(Position(1,-3,-5)), 1-15-10, epsilon(4))
  }
  test("Velocity.Direction"){
    assertAlmostEquals(Velocity(.1,0,0).direction, Position(1,0,0), epsilon(4))
    //assertAlmostEquals(Velocity(0,0,0).direction.length, 1, epsilon(4)) //Arbitrary, but make sure length is still 1
    assertAlmostEquals(Velocity(-.1,0,0).direction, Position(-1,0,0), epsilon(4))
    assertAlmostEquals(Velocity(.1,.1,0).direction, Position(.70711,.70711,0), epsilon(4))
    assertAlmostEquals(Velocity(.1,0,-.1).direction, Position(.70711,0,-.70711), epsilon(4))
    assertAlmostEquals(Velocity(-0.0588690443588897, -0.0424303165787665, 0.0847967714413384).direction,
      Position(-0.527461677578867, -0.380172061676401, 0.75977192775622), epsilon(4))
    assertAlmostEquals(Velocity(0.0964542823147781, -0.0529483276568627, -0.0314994329815463).direction,
      Position(0.842751731768307, -0.462626373409032, -0.275220561055905), epsilon(4))
    assertAlmostEquals(Velocity(-0.00595767010152027, 0.0425358034314269, -0.0753701754609321).direction,
      Position(-0.0686768177271948, 0.490329872141537, -0.868826859156949), epsilon(4))
    assertAlmostEquals(Velocity(0.0421999045785647, 0.0461731640117067, -0.0389784101175201).direction,
      Position(0.572568184172217, 0.626477356754442, -0.528858956573204), epsilon(4))
    assertAlmostEquals(Velocity(0.0243315268229186, 0.0548418168885566, 0.0194573030910375).direction,
      Position(0.385766188768937, 0.869494086426784, 0.308487408611016), epsilon(4))
    assertAlmostEquals(Velocity(0.0962780154565056, -0.0308881666092364, -0.0172524463780638).direction,
      Position(0.938630934219452, -0.301134049588416, -0.168197067467896), epsilon(4))
    assertAlmostEquals(Velocity(-0.0519206396434244, -0.0872116700617874, 0.0727906206204176).direction,
      Position(-0.415696440641791, -0.698249888215748, 0.582789466998539), epsilon(4))
    assertAlmostEquals(Velocity(-0.0704821394017934, -0.0803776269851338, 0.0147140099279307).direction,
      Position(-0.653150280074024, -0.744850681642188, 0.136353121329846), epsilon(4))
    assertAlmostEquals(Velocity(0.0732225169964744, -0.0772141885718619, -0.068424472083661).direction,
      Position(0.578775784483816, -0.610327320025238, -0.54085039865563), epsilon(4))
  }
  test("Lorentz transform"){
    //Do some 1D tests
    var v = Velocity(.5,0,0)
    assertAlmostEquals(Position(1,1,1).boost(v,0), Position(1*v.gamma,1,1), epsilon(4))
    assertAlmostEquals(Position(1,1,1).boost(v,10), Position((1-10*0.5)*v.gamma,1,1), epsilon(4))

  }
}