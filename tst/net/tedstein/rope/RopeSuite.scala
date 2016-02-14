package net.tedstein.rope

import scala.math.{abs, pow}
import org.scalatest.FunSuite
import net.tedstein.rope.physics.Dimensions.{Position, Velocity}
import net.tedstein.rope.physics.Vector3d

class RopeSuite extends FunSuite {
  def epsilon(magnitude: Int) = pow(10, -magnitude)

  def assertAlmostEquals(a: Double, b: Double, tolerance: Double) = {
    assert(abs(a - b) < tolerance)
  }
  def assertAlmostEquals(a: Position, b: Position, tolerance: Double) = {
    assert(abs(a.x - b.x) < tolerance)
    assert(abs(a.y - b.y) < tolerance)
    assert(abs(a.z - b.z) < tolerance)
  }
  def assertAlmostEquals(a: Velocity, b: Velocity, tolerance: Double) = {
    assert(abs(a.x - b.x) < tolerance)
    assert(abs(a.y - b.y) < tolerance)
    assert(abs(a.z - b.z) < tolerance)
  }
  def assertAlmostEquals(a: Vector3d, b: Vector3d, tolerance: Double) = {
    assert(abs(a.x - b.x) < tolerance)
    assert(abs(a.y - b.y) < tolerance)
    assert(abs(a.z - b.z) < tolerance)
  }
}