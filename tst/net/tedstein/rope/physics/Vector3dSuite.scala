package net.tedstein.rope.physics

import net.tedstein.rope.RopeSuite
//import net.tedstein.rope.physics.Vector3d

class Vector3dSuite extends RopeSuite {
  val a = Vector3d(3,4,5)
  val b = Vector3d(5,4,3)
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
}
