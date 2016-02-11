package net.tedstein.rope.graphics

/**
  * Created by ruba on 1/21/16.
  */
case class Vector2f(x: Float = 0.0f, y: Float = 0.0f) {
  override def toString: String = {
    val s = new StringBuilder
    s.append("(" + this.x + ", " + this.y + ")")
    s.toString()
  }

  def lengthSquared: Float = {
    x * x + y * y
  }

  def length: Float = {
    Math.sqrt(lengthSquared).toFloat
  }

  def add(other: Vector2f): Vector2f = {
    Vector2f(x + other.x, y + other.y)
  }

  def subtract(other: Vector2f): Vector2f = {
    Vector2f(x - other.x, y - other.y)
  }

  def scale(scalar: Float): Vector2f = {
    Vector2f(x * scalar, y * scalar)
  }

  def divide(scalar: Float): Vector2f = {
    Vector2f(x / scalar, y / scalar)
  }

  def negate: Vector2f = {
    scale(-1.0f)
  }

  def normalize: Vector2f = {
    divide(length)
  }

  def dot(other: Vector2f): Float = {
    x * other.x + y * other.y
  }

  def lerp(other: Vector2f, value: Float): Vector2f = {
    this.scale(1.0f - value).add(other.scale(value))
  }

}

  object Vector2f {
    val Zero = Vector2f(0.0f, 0.0f)
  }


