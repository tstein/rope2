package net.tedstein.rope

case class Vector3f(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f) {

  def lengthSquared(): Float = {
    x * x + y * y + z * z
  }

  def length(): Float = {
    Math.sqrt(lengthSquared()).toFloat
  }

  def add(other: Vector3f): Vector3f = {
    val res = Vector3f(x + other.x, y + other.y, z + other.z)
    println(res.x)
    res
  }

  def subtract(other: Vector3f): Vector3f = {
    Vector3f(x - other.x, y - other.y, z - other.z)
  }

  def scale(scalar: Float): Vector3f = {
    Vector3f(x * scalar, y * scalar, z * scalar)
  }

  def divide(scalar: Float): Vector3f = {
    Vector3f(x / scalar, y / scalar, z / scalar)
  }

  def negate(): Vector3f = {
    scale(-1.0f)
  }

  def normalize(): Vector3f = {
    divide(length())
  }

  def dot(other: Vector3f): Float = {
    x * other.x + y * other.y + z * other.z
  }

  def cross(other: Vector3f): Vector3f = {
    Vector3f(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x)
  }

  def lerp(other: Vector3f, value: Float): Vector3f = {
    this.scale(1.0f - value).add(other.scale(value))
  }

}


