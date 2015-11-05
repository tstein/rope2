package net.tedstein.rope

import java.nio.FloatBuffer

import org.lwjgl.BufferUtils._

/**
 * Created by ruba on 11/2/15.
 */
object Matrix4f {
  val matrix = Array.ofDim[Float](4, 4)

  def setIdentity(): Array[Array[Float]] = {
    matrix(0)(0) = 1
    matrix(1)(1) = 1
    matrix(2)(2) = 1
    matrix(3)(3) = 1
    matrix(0)(1) = 0
    matrix(0)(2) = 0
    matrix(0)(3) = 0
    matrix(1)(0) = 0
    matrix(1)(2) = 0
    matrix(1)(3) = 0
    matrix(2)(0) = 0
    matrix(2)(1) = 0
    matrix(2)(3) = 0
    matrix(3)(0) = 0
    matrix(3)(1) = 0
    matrix(3)(2) = 0
    return matrix
  }

  def setRowColumnValue(matrix: Array[Array[Float]], row: Int, column: Int, value: Float): Unit = {
    matrix(row)(column) = value
  }

  def getFloatBuffer(matrix: Array[Array[Float]]): FloatBuffer  = {
    val floatBuffer = createFloatBuffer(16)
    floatBuffer.put(matrix(0)(0)).put(matrix(0)(1)).put(matrix(0)(2)).put(matrix(0)(3))
    floatBuffer.put(matrix(1)(0)).put(matrix(1)(1)).put(matrix(1)(2)).put(matrix(1)(3))
    floatBuffer.put(matrix(2)(0)).put(matrix(2)(1)).put(matrix(2)(2)).put(matrix(2)(3))
    floatBuffer.put(matrix(3)(0)).put(matrix(3)(1)).put(matrix(3)(2)).put(matrix(3)(3))
    floatBuffer.flip()
    return floatBuffer
  }

}
