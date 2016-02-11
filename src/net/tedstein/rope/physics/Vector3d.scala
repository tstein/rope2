package net.tedstein.rope.physics

import scala.math.{pow, sqrt}

case class Vector3d(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0) {

  //Add
  def + (arg: Vector3d): Vector3d = {
    Vector3d(x + arg.x, y + arg.y, z + arg.z)
  }
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
  val Zero = Vector3d(0.0, 0.0, 0.0)
}
