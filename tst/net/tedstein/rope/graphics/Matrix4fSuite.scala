package net.tedstein.rope.graphics

import org.scalatest.FunSuite

class Matrix4fSuite extends FunSuite {
  test("getFloatBuffer goes in column-major order") {
    val (tx, ty, tz) = (1001, 1002, 1003)
    val matrix = Matrix4f(m30 = 1001, m31 = 1002, m32 = 1003)
    val floats = Matrix4f.getFloatBuffer(matrix)
    assert(floats.get(12) == tx)
    assert(floats.get(13) == ty)
    assert(floats.get(14) == tz)
  }
}
