package net.tedstein.rope

import scala.math.{abs, pow}
import org.scalatest.FunSuite


class RopeSuite extends FunSuite {
  def epsilon(magnitude: Int) = pow(10, -magnitude)

  def assertAlmostEquals(a: Double, b: Double, tolerance: Double) = {
    assert(abs(a - b) < tolerance)
  }
}