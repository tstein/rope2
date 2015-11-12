package net.tedstein.rope

import java.nio.FloatBuffer

import org.lwjgl.BufferUtils

/**
 * Created by ruba on 11/2/15.
 */


case class Matrix4f (){
  val matrix = Array.ofDim[Float](4, 4)
  matrix(0)(0) = 0.0f
  matrix(1)(1) = 0.0f
  matrix(2)(2) = 0.0f
  matrix(3)(3) = 0.0f
  matrix(0)(1) = 0.0f
  matrix(0)(2) = 0.0f
  matrix(0)(3) = 0.0f
  matrix(1)(0) = 0.0f
  matrix(1)(2) = 0.0f
  matrix(1)(3) = 0.0f
  matrix(2)(0) = 0.0f
  matrix(2)(1) = 0.0f
  matrix(2)(3) = 0.0f
  matrix(3)(0) = 0.0f
  matrix(3)(1) = 0.0f
  matrix(3)(2) = 0.0f

  def setIdentity(): Unit = {
    //do I need "this" here? I don't think I do
  }

  def getFloatBuffer(): FloatBuffer  = {
    val floatBuffer = BufferUtils.createFloatBuffer(16)
    floatBuffer.put(matrix(0)(0)).put(matrix(0)(1)).put(matrix(0)(2)).put(matrix(0)(3))
    floatBuffer.put(matrix(1)(0)).put(matrix(1)(1)).put(matrix(1)(2)).put(matrix(1)(3))
    floatBuffer.put(matrix(2)(0)).put(matrix(2)(1)).put(matrix(2)(2)).put(matrix(2)(3))
    floatBuffer.put(matrix(3)(0)).put(matrix(3)(1)).put(matrix(3)(2)).put(matrix(3)(3))
    floatBuffer.flip()
    return floatBuffer
  }
}
