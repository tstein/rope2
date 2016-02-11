package net.tedstein.rope.physics

import scala.math.{pow, sqrt}

class Vector3d {
  var x: Double = 0
  var y: Double = 0
  var z: Double = 0

  //Add
  def + (arg: Vector3d): Vector3d = {
    Vector3d(x + arg.x, y + arg.y, z + arg.z)
  }
  def add(arg: Vector3d): Vector3d = this + arg
  //Subtract
  def - (arg: Vector3d): Vector3d = {
    Vector3d(x - arg.x, y - arg.y, z - arg.z)
  }
  //Unary negative prefix
  def unary_- : Vector3d = {
    Vector3d(-x, -y, -z)
  }
  //Scalar multiply
  def * (arg: Double): Vector3d = {
    Vector3d(x * arg, y * arg, z * arg)
  }
  //Scalar divide
  def / (arg: Double): Vector3d = {
    Vector3d(x / arg, y / arg, z / arg)
  }
  //Dot product
  def * (arg: Vector3d): Double = {
    x * arg.x + y * arg.y + z * arg.z
  }
  def dot (arg: Vector3d): Double = this * arg
  //Cross product
  def cross (arg: Vector3d): Vector3d = {
    Vector3d(y * arg.z - z * arg.y, z * arg.x - x * arg.z, x * arg.y - y * arg.x)
  }

  //A few others:
  //Lengths
  def lengthSquared: Double = {
    x * x + y * y + z * z
  }
  def length: Double = {
    Math.sqrt(lengthSquared)
  }

  //Normalize
  def normalize: Vector3d = {
    //If length is zero, just return ANY vector
    if(this.length==0)
      Vector3d(1,0,0)
    else
      this / this.length
  }
  //Lerp: implement/copy when needed
  //Tostring: implement/copy when needed
}

object Vector3d {
  //Add the apply magic to mimic a case class
  def apply(xc: Double, yc: Double, zc: Double): Vector3d = {
    val p = new Vector3d
    p.x = xc
    p.y = yc
    p.z = zc
    p
  }
  val Zero = Vector3d(0.0, 0.0, 0.0)
}
