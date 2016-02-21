package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
//import net.tedstein.rope.physics.Vector3d

class Vector3dSuite extends RopeSuite {
  val a = Vector3d(3,4,5)
  val b = Vector3d(5,4,3)
  val r1 = Vector3d(1-2*math.random, 1-2*math.random, 1-2*math.random)
  val r2 = Vector3d(1-2*math.random, 1-2*math.random, 1-2*math.random)
  val tol = epsilon(8)
  //Equaltiy / constructor
  test("Equality and constructors"){
    assert(a == a)
    assert(Vector3d(1,2,3) == new Vector3d(1,2,3))
  }
  //Add
  test("Addition"){
    assertAlmostEquals(a + b, Vector3d(8,8,8), tol)
    assertAlmostEquals(a + b, a.add(b), tol)
  }
  //Subtract
  test("Subtraction"){
    assertAlmostEquals(a - b, Vector3d(-2,0,2), tol)
  }
  //Unary negative prefix
  test("Unary negative prefix"){
    assertAlmostEquals(-a + b, b - a, tol)
  }
  //Scalar multiply
  test("Scalar multiply"){
    assertAlmostEquals(a * 5, a + a + a + a + a, tol)
  }
  //Scalar divide
  test("Scalar divide"){
    assertAlmostEquals( (a + a + a) / 3, a, tol)
  }
  //Dot product
  test("Dot product"){
    assertAlmostEquals( a * a, 9 + 16 + 25, tol)
    assertAlmostEquals( a * a, a.dot(a), tol)
  }
  //Cross product
  test("Cross product"){
    assertAlmostEquals( a.cross(a), Vector3d.Zero, tol)
    assertAlmostEquals( a.cross(b), Vector3d(12-20, 25-9, 12-20), tol)
    assertAlmostEquals( a.cross(b), -b.cross(a), tol)
  }
  //Lengths
  test("Length Squared"){
    assertAlmostEquals(a.lengthSquared, 50, tol)
  }
  test("Length"){
    assertAlmostEquals(a.length, math.sqrt(50), tol)
  }
  //Normalize
  test("Normalize"){
    assertAlmostEquals(a.normalize, a / math.sqrt(50), tol)
  }
  test("Normalizing Vector3d.Zero returns length==1"){
    assertAlmostEquals(Vector3d.Zero.normalize.length, 1, tol)
  }
  test("lerp"){
    assertAlmostEquals(a.lerp(b,0), a, tol)
    assertAlmostEquals(a.lerp(b,1), b, tol)
    val rand = math.random
    for(_ <- 1 to 10)
      assertAlmostEquals(a.lerp(b,rand), b.lerp(a,1-rand), tol)
  }
  test("Rotation"){
    val angle90 = math.Pi / 2
    val randAngle = math.random * math.Pi * 15
    //90 deg
    assertAlmostEquals(Vector3d(5,1,1).rotate(angle90, Vector3d(0,0,123)), Vector3d(-1,5,1), tol)
    //360 deg
    assertAlmostEquals(a.rotate(angle90*4, b), a, tol)
    //length better not change
    assertAlmostEquals(r1.rotate(randAngle, r2).length, r1.length, tol)
  }
  test("RandomDir"){
    //Length should be 1
    assertAlmostEquals(Vector3d.randomDir.length, 1, tol)
    //Sum of a bunch of these should be close to 0
    var sum = Vector3d(0,0,0)
    val size: Int = 1000
    for(_ <- 1 to size)
      sum = sum + Vector3d.randomDir
    //Check that it is within it to something reasonable, say 7 sigma? (7 sigma is 1 in 400 billion or so)
    assertAlmostEquals(sum, Vector3d(0,0,0), math.sqrt(size) * 7)
  }
  test("Projection"){
    assertAlmostEquals(a.proj(Vector3d(0,77,0)), Vector3d(0,4,0), tol)
    assertAlmostEquals(a.proj(Vector3d(-10,0,0)), Vector3d(3,0,0), tol)
    assertAlmostEquals(a.proj(Vector3d(0,0,.1)), Vector3d(0,0,5), tol)
    assertAlmostEquals(a.proj(b) * b, a * b, tol)
    assertAlmostEquals(a.proj(b).cross(b), Vector3d.Zero, tol)
  }
  test("Perpendicular"){
    assertAlmostEquals(a.perp(Vector3d(0,77,0)), Vector3d(3,0,5), tol)
    assertAlmostEquals(a.perp(Vector3d(-10,0,0)), Vector3d(0,4,5), tol)
    assertAlmostEquals(a.perp(Vector3d(0,0,.1)), Vector3d(3,4,0), tol)
    assertAlmostEquals(a.perp(b) * b, 0, tol)
  }
}
