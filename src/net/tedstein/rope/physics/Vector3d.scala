package net.tedstein.rope.physics

import scala.math.{pow, sqrt, cos, sin}

//class Vector3d (xc: Double, yc: Double, zc: Double) {
case class Vector3d (x: Double, y: Double, z: Double) {
//  var x: Double = xc
//  var y: Double = yc
//  var z: Double = zc
  //Add
  def + (arg: Vector3d): Vector3d = {
    new Vector3d(x + arg.x, y + arg.y, z + arg.z)
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
  //Lerp: linear interpolate
  def lerp(other: Vector3d, value: Double): Vector3d = {
    this * (1.0 - value) + (other * value)
  }
  //Tostring
  override def toString: String = {
    val s = new StringBuilder
    s.append("(" + this.x + ", " + this.y + ", " + this.z + ")")
    s.toString()
  }
  //Rotate: Rotate by angle about an axis
  def rotate(angle: Double, axisInput: Vector3d): Vector3d = {
    //Perhaps better implementation, though substantially less clean:
    //https://en.wikipedia.org/wiki/Rotation_matrix#Rotation_matrix_from_axis_and_angle
    val axis = axisInput.normalize
    val projection = axis * (axis * this)
    val perpComponent = axis.cross(this)
    (this - projection) * cos(angle) + perpComponent * sin(angle) + projection
  }
}

object Vector3d {
  //Add the apply magic to mimic a case class
//  def apply(xc: Double, yc: Double, zc: Double): Vector3d = {
    //Okay, this seems somewhat dumb
//    new Vector3d(xc,yc,zc)
//  }
  val Zero = Vector3d(0.0, 0.0, 0.0)
  //Random direction, uniformly distributed on a sphere
  //By approximating 0.8314*atanh as invErf
  private def atanh(x: Double): Double = 0.5 * (math.log(x+1+1E-7) - math.log(-x+1+1E-7))
  def randomDir: Vector3d = Vector3d(
    0.831406 * atanh(math.random * 2 - 1),
    0.831406 * atanh(math.random * 2 - 1),
    0.831406 * atanh(math.random * 2 - 1)
  ).normalize
}
