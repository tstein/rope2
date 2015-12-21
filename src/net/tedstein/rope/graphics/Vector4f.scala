package net.tedstein.rope.graphics

case class Vector4f(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f, w: Float = 0.0f) {

  def lengthSquared(): Float ={
    x * x + y * y + z * z + w * w
  }

  def length(): Float = {
    Math.sqrt(lengthSquared()).toFloat
  }

  def add(other: Vector4f): Vector4f = {
    val res = Vector4f(x + other.x, y + other.y, z + other.z, w + other.w)
    println(res.x)
    res
  }

  def subtract(other: Vector4f): Vector4f = {
    Vector4f(x - other.x, y - other.y, z - other.z, w - other.w)
  }

  def scale(scalar: Float): Vector4f = {
    Vector4f(x * scalar, y * scalar, z * scalar, w * scalar)
  }

  def divide(scalar: Float): Vector4f = {
    Vector4f(x / scalar, y / scalar, z / scalar, w / scalar)
  }

  def negate(): Vector4f = {
    scale(-1.0f)
  }

  def normalize(): Vector4f = {
    divide(length())
  }

  def dot(other: Vector4f): Float = {
    x * other.x + y * other.y + z * other.z + w * other.z
  }

  def lerp(other: Vector4f, value: Float): Vector4f = {
    this.scale(1.0f - value).add(other.scale(value))
  }


}

