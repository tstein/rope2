package net.tedstein.rope.graphics

import java.nio.FloatBuffer

import org.lwjgl.BufferUtils

/**
 * 4x4 Matrix in a column-major order
 * /* ::-------------------------------------------------------------------------::
 *The first index indicates the COLUMN NUMBER.
 * The second is the ROW NUMBER.
 *
 * | A E I M |   | m00 m10 m20 m30 |
 * | B F J N | = | m01 m11 m21 m31 |
 * | C G K O |   | m02 m12 m22 m32 |
 * | D H L P |   | m03 m13 m23 m33 |
 */
 * */
class Matrix4f() {
  val matrix = Array.ofDim[Float](4, 4)

  override def toString: String = {
    val s = new StringBuilder
    s.append("{")
    for (i <- 0 to 3) {
      for (j <- 0 to 3) {
        s.append(s" ${matrix(j)(i)},")
      }
      if (i < 3) s.append{"\n "} else s.append("}\n")
    }
    s.toString()
  }
}

object Matrix4f {

  def apply(m00: Float = 1.0f, m01: Float = 0.0f, m02: Float = 0.0f, m03: Float = 0.0f,
            m10: Float = 0.0f, m11: Float = 1.0f, m12: Float = 0.0f, m13: Float = 0.0f,
            m20: Float = 0.0f, m21: Float = 0.0f, m22: Float = 1.0f, m23: Float = 0.0f,
            m30: Float = 0.0f, m31: Float = 0.0f, m32: Float = 0.0f, m33: Float = 1.0f): Matrix4f = {

    val mat4 = new Matrix4f()
    mat4.matrix(0)(0) = m00
    mat4.matrix(0)(1) = m01
    mat4.matrix(0)(2) = m02
    mat4.matrix(0)(3) = m03

    mat4.matrix(1)(0) = m10
    mat4.matrix(1)(1) = m11
    mat4.matrix(1)(2) = m12
    mat4.matrix(1)(3) = m13

    mat4.matrix(2)(0) = m20
    mat4.matrix(2)(1) = m21
    mat4.matrix(2)(2) = m22
    mat4.matrix(2)(3) = m23

    mat4.matrix(3)(0) = m30
    mat4.matrix(3)(1) = m31
    mat4.matrix(3)(2) = m32
    mat4.matrix(3)(3) = m33

    mat4
  }


  def apply(col0: Vector4f, col1: Vector4f, col2: Vector4f, col3: Vector4f): Matrix4f = {

    val mat4 = Matrix4f()
    mat4.matrix(0)(0) = col0.x
    mat4.matrix(0)(1) = col0.y
    mat4.matrix(0)(2) = col0.z
    mat4.matrix(0)(3) = col0.w

    mat4.matrix(1)(0) = col1.x
    mat4.matrix(1)(1) = col1.y
    mat4.matrix(1)(2) = col1.z
    mat4.matrix(1)(3) = col1.w

    mat4.matrix(2)(0) = col2.x
    mat4.matrix(2)(1) = col2.y
    mat4.matrix(2)(2) = col2.z
    mat4.matrix(2)(3) = col2.w

    mat4.matrix(3)(0) = col3.x
    mat4.matrix(3)(1) = col3.y
    mat4.matrix(3)(2) = col3.z
    mat4.matrix(3)(3) = col3.w

    mat4
  }

  def apply(diagonalValue: Float): Matrix4f = {

    val mat4 = Matrix4f()
    mat4.matrix(0)(0) = diagonalValue
    mat4.matrix(1)(1) = diagonalValue
    mat4.matrix(2)(2) = diagonalValue
    mat4.matrix(3)(3) = diagonalValue

    mat4.matrix(0)(1) = 0.0f
    mat4.matrix(0)(2) = 0.0f
    mat4.matrix(0)(3) = 0.0f

    mat4.matrix(1)(0) = 0.0f
    mat4.matrix(1)(2) = 0.0f
    mat4.matrix(1)(3) = 0.0f

    mat4.matrix(2)(0) = 0.0f
    mat4.matrix(2)(1) = 0.0f
    mat4.matrix(2)(3) = 0.0f

    mat4.matrix(3)(0) = 0.0f
    mat4.matrix(3)(1) = 0.0f
    mat4.matrix(3)(2) = 0.0f

    mat4
  }

  def getFloatBuffer(m: Matrix4f): FloatBuffer   = {

    val floatBuffer = BufferUtils.createFloatBuffer(16)
    floatBuffer.put(m.matrix(0)(0))
    floatBuffer.put(m.matrix(0)(1))
    floatBuffer.put(m.matrix(0)(2))
    floatBuffer.put(m.matrix(0)(3)) //column 1


    floatBuffer.put(m.matrix(1)(0))
    floatBuffer.put(m.matrix(1)(1))
    floatBuffer.put(m.matrix(1)(2))
    floatBuffer.put(m.matrix(1)(3)) //column 2

    floatBuffer.put(m.matrix(2)(0))
    floatBuffer.put(m.matrix(2)(1))
    floatBuffer.put(m.matrix(2)(2))
    floatBuffer.put(m.matrix(2)(3)) //column 3

    floatBuffer.put(m.matrix(3)(0))
    floatBuffer.put(m.matrix(3)(1))
    floatBuffer.put(m.matrix(3)(2))
    floatBuffer.put(m.matrix(3)(3)) //column 4
    floatBuffer.flip()
    floatBuffer
  }

  def add(m1: Matrix4f, m2: Matrix4f) : Matrix4f = {
    val result = Matrix4f()
    for (i <- 0 to 3; j <- 0 to 3) {
      result.matrix(i)(j) = m1.matrix(i)(j) + m2.matrix(i)(j)
    }
    result
  }

  def multiply(m: Matrix4f, scalar: Int): Matrix4f = {
    val result = Matrix4f()
    for (i <- 0 to 3; j <- 0 to 3) {
      result.matrix(i)(j) = m.matrix(i)(j) * scalar
    }
    result
  }

  def subtract(m1: Matrix4f, m2: Matrix4f): Matrix4f = {
    add(m1, negate(m2))
  }

  def negate(m: Matrix4f): Matrix4f = {
    multiply(m, -1)
  }

  def multiply(m: Matrix4f, v: Vector4f): Vector4f = {
    var x, y, z, w: Float = 0.0f
    x = m.matrix(0)(0) * v.x + m.matrix(0)(1) * v.y + m.matrix(0)(2) * v.z + m.matrix(0)(3) * v.w
    y = m.matrix(1)(0) * v.x + m.matrix(1)(1) * v.y + m.matrix(1)(2) * v.z + m.matrix(1)(3) * v.w
    z = m.matrix(2)(0) * v.x + m.matrix(2)(1) * v.y + m.matrix(2)(2) * v.z + m.matrix(2)(3) * v.w
    w = m.matrix(3)(0) * v.x + m.matrix(3)(1) * v.y + m.matrix(3)(2) * v.z + m.matrix(3)(3) * v.w
    Vector4f(x, y, z, w)
  }

  def multiply(left: Matrix4f, right: Matrix4f): Matrix4f = {
    val nm00 = left.matrix(0)(0) * right.matrix(0)(0) + left.matrix(1)(0) * right.matrix(0)(1) + left.matrix(2)(0) * right.matrix(0)(2) + left.matrix(3)(0) * right.matrix(0)(3)
    val nm01 = left.matrix(0)(1) * right.matrix(0)(0) + left.matrix(1)(1) * right.matrix(0)(1) + left.matrix(2)(1) * right.matrix(0)(2) + left.matrix(3)(1) * right.matrix(0)(3)
    val nm02 = left.matrix(0)(2) * right.matrix(0)(0) + left.matrix(1)(2) * right.matrix(0)(1) + left.matrix(2)(2) * right.matrix(0)(2) + left.matrix(3)(2) * right.matrix(0)(3)
    val nm03 = left.matrix(0)(3) * right.matrix(0)(0) + left.matrix(1)(3) * right.matrix(0)(1) + left.matrix(2)(3) * right.matrix(0)(2) + left.matrix(3)(3) * right.matrix(0)(3)
    val nm10 = left.matrix(0)(0) * right.matrix(1)(0) + left.matrix(1)(0) * right.matrix(1)(1) + left.matrix(2)(0) * right.matrix(1)(2) + left.matrix(3)(0) * right.matrix(1)(3)
    val nm11 = left.matrix(0)(1) * right.matrix(1)(0) + left.matrix(1)(1) * right.matrix(1)(1) + left.matrix(2)(1) * right.matrix(1)(2) + left.matrix(3)(1) * right.matrix(1)(3)
    val nm12 = left.matrix(0)(2) * right.matrix(1)(0) + left.matrix(1)(2) * right.matrix(1)(1) + left.matrix(2)(2) * right.matrix(1)(2) + left.matrix(3)(2) * right.matrix(1)(3)
    val nm13 = left.matrix(0)(3) * right.matrix(1)(0) + left.matrix(1)(3) * right.matrix(1)(1) + left.matrix(2)(3) * right.matrix(1)(2) + left.matrix(3)(3) * right.matrix(1)(3)
    val nm20 = left.matrix(0)(0) * right.matrix(2)(0) + left.matrix(1)(0) * right.matrix(2)(1) + left.matrix(2)(0) * right.matrix(2)(2) + left.matrix(3)(0) * right.matrix(2)(3)
    val nm21 = left.matrix(0)(1) * right.matrix(2)(0) + left.matrix(1)(1) * right.matrix(2)(1) + left.matrix(2)(1) * right.matrix(2)(2) + left.matrix(3)(1) * right.matrix(2)(3)
    val nm22 = left.matrix(0)(2) * right.matrix(2)(0) + left.matrix(1)(2) * right.matrix(2)(1) + left.matrix(2)(2) * right.matrix(2)(2) + left.matrix(3)(2) * right.matrix(2)(3)
    val nm23 = left.matrix(0)(3) * right.matrix(2)(0) + left.matrix(1)(3) * right.matrix(2)(1) + left.matrix(2)(3) * right.matrix(2)(2) + left.matrix(3)(3) * right.matrix(2)(3)
    val nm30 = left.matrix(0)(0) * right.matrix(3)(0) + left.matrix(1)(0) * right.matrix(3)(1) + left.matrix(2)(0) * right.matrix(3)(2) + left.matrix(3)(0) * right.matrix(3)(3)
    val nm31 = left.matrix(0)(1) * right.matrix(3)(0) + left.matrix(1)(1) * right.matrix(3)(1) + left.matrix(2)(1) * right.matrix(3)(2) + left.matrix(3)(1) * right.matrix(3)(3)
    val nm32 = left.matrix(0)(2) * right.matrix(3)(0) + left.matrix(1)(2) * right.matrix(3)(1) + left.matrix(2)(2) * right.matrix(3)(2) + left.matrix(3)(2) * right.matrix(3)(3)
    val nm33 = left.matrix(0)(3) * right.matrix(3)(0) + left.matrix(1)(3) * right.matrix(3)(1) + left.matrix(2)(3) * right.matrix(3)(2) + left.matrix(3)(3) * right.matrix(3)(3)

    Matrix4f(nm00, nm01, nm02, nm03,
             nm10, nm11, nm12, nm13,
             nm20, nm21, nm22, nm23,
             nm30, nm31, nm32, nm33)
  }

  def transpose(m: Matrix4f): Matrix4f = {
    val result = Matrix4f()
    result.matrix(0)(0) = m.matrix(0)(0)
    result.matrix(1)(0) = m.matrix(0)(1)
    result.matrix(2)(0) = m.matrix(0)(2)
    result.matrix(3)(0) = m.matrix(0)(3)

    result.matrix(0)(1) = m.matrix(1)(0)
    result.matrix(1)(1) = m.matrix(1)(1)
    result.matrix(2)(1) = m.matrix(1)(2)
    result.matrix(3)(1) = m.matrix(1)(3)

    result.matrix(0)(2) = m.matrix(2)(0)
    result.matrix(1)(2) = m.matrix(2)(1)
    result.matrix(2)(2) = m.matrix(2)(2)
    result.matrix(3)(2) = m.matrix(2)(3)

    result.matrix(0)(3) = m.matrix(3)(0)
    result.matrix(1)(3) = m.matrix(3)(1)
    result.matrix(2)(3) = m.matrix(3)(2)
    result.matrix(3)(3) = m.matrix(3)(3)

    result
  }

  def invert(m: Matrix4f): Matrix4f = {
    val a: Float = m.matrix(0)(0) * m.matrix(1)(1) - m.matrix(0)(1) * m.matrix(1)(0)
    val b: Float = m.matrix(0)(0) * m.matrix(1)(2) - m.matrix(0)(2) * m.matrix(1)(0)
    val c: Float = m.matrix(0)(0) * m.matrix(1)(3) - m.matrix(0)(3) * m.matrix(1)(0)

    val d: Float = m.matrix(0)(1) * m.matrix(1)(2) - m.matrix(0)(2) * m.matrix(1)(1)
    val e: Float = m.matrix(0)(1) * m.matrix(1)(3) - m.matrix(0)(3) * m.matrix(1)(2)
    val f: Float = m.matrix(0)(2) * m.matrix(1)(3) - m.matrix(0)(3) * m.matrix(1)(2)
    val g: Float = m.matrix(2)(0) * m.matrix(3)(1) - m.matrix(2)(1) * m.matrix(3)(0)
    val h: Float = m.matrix(2)(1) * m.matrix(3)(2) - m.matrix(2)(2) * m.matrix(3)(0)

    val i: Float = m.matrix(2)(0) * m.matrix(3)(3) - m.matrix(2)(3) * m.matrix(3)(0)
    val j: Float = m.matrix(2)(1) * m.matrix(3)(2) - m.matrix(2)(2) * m.matrix(3)(1)
    val k: Float = m.matrix(2)(1) * m.matrix(3)(3) - m.matrix(2)(3) * m.matrix(3)(1)
    val l: Float = m.matrix(2)(2) * m.matrix(3)(3) - m.matrix(2)(3) * m.matrix(3)(2)

    var det: Float = a * l - b * k + c * j + d * i - e * h + f * g

    det = 1.0f / det

    Matrix4f((m.matrix(1)(1) * l - m.matrix(1)(2) * k + m.matrix(1)(3) * j) * det,
      (-m.matrix(0)(1) * l + m.matrix(0)(2) * k - m.matrix(1)(3) * j) * det,
      (m.matrix(3)(1) * f - m.matrix(3)(2) * 2 + m.matrix(3)(3) * d) * det,
      (-m.matrix(2)(1) * f + m.matrix(2)(2) * e - m.matrix(2)(3) * d) * det,
      (-m.matrix(1)(0) * l + m.matrix(1)(2) * i - m.matrix(1)(3) * h) * det,
      (m.matrix(0)(0) * l - m.matrix(0)(2) * i + m.matrix(0)(3) * h) * det,
      (-m.matrix(3)(0) * f + m.matrix(3)(2) * c - m.matrix(3)(3) * b) * det,
      (m.matrix(2)(0) * f - m.matrix(2)(2) * c + m.matrix(2)(3) * b) * det,
      (m.matrix(1)(0) * k - m.matrix(1)(1) * i + m.matrix(1)(3) * g) * det,
      (-m.matrix(0)(0) * k + m.matrix(0)(1) * i - m.matrix(0)(3) * g) * det,
      (m.matrix(3)(0) * e - m.matrix(3)(1) * c + m.matrix(3)(3) * a) * det,
      (-m.matrix(2)(0) * e + m.matrix(2)(1) * c - m.matrix(2)(3) * a) * det,
      (-m.matrix(1)(0) * j + m.matrix(1)(1) * h - m.matrix(1)(2) * g) * det,
      (m.matrix(0)(0) * j - m.matrix(0)(1) * h + m.matrix(0)(2) * g) * det,
      (-m.matrix(3)(0) * d + m.matrix(3)(1) * b - m.matrix(3)(2) * a) * det,
      (m.matrix(2)(0) * d - m.matrix(2)(1) * b + m.matrix(2)(2) * a) * det)

  }


}
