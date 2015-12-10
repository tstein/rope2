package net.tedstein.rope


case class Camera() {
  var pos = Vector3f(0.0f, 0.0f, 1.0f)
  var horizontalAngle: Float = 0.0f
  var verticalAngle: Float = 0.0f
  var fov: Float = 45.0f
  var zNear: Float = 0.1f
  var zFar: Float = 100.0f
  var aspectRatio: Float = 4.0f / 3.0f
  val maxVerticalAngle: Float = 80.0f

  def position(): Vector3f = {
    pos
  }

  def setPosition(p: Vector3f): Unit = {
    pos = p
  }

  def offsetPosition(offset: Vector3f): Unit = {
    pos.add(offset)
  }

  def fieldOfView(): Float = {
    fov
  }

  def setFieldOfView(f: Float): Unit = {
    fov = f
  }

  def nearPlane(): Float = {
    zNear
  }

  def farPlane(): Float = {
    zFar
  }

  def setNearAndFarPlanes(nearPlane: Float, farPlane: Float): Unit = {
    assert(nearPlane > 0.0f)
    assert(farPlane > nearPlane)
    zNear = nearPlane
    zFar = farPlane
  }

  def orientation(): Matrix4f = {
    val rot = Matrix4f()
    println("vertical angle: " + verticalAngle)
    println("horinzontal angle: " + horizontalAngle)
    Transformations.rotate(rot, verticalAngle, 1.0f, 0.0f, 0.0f)
    Transformations.rotate(rot, horizontalAngle, 0.0f, 1.0f, 0.0f)
    rot
  }

  def offsetOrientation(upAngle: Float, rightAngle: Float): Unit = {
    horizontalAngle += rightAngle
    verticalAngle += upAngle
    normalizeAngles()
  }

  def normalizeAngles(): Unit = {
    horizontalAngle = horizontalAngle % 360.0f

    if (horizontalAngle < 0.0f)
      horizontalAngle += 360.0f

    if (verticalAngle > maxVerticalAngle) {
      verticalAngle = maxVerticalAngle
    }
    else if (verticalAngle < -maxVerticalAngle)
      verticalAngle = -maxVerticalAngle
  }

  def lookAt(target: Vector3f) = {
    assert(pos != target)
    val direction: Vector3f = target.subtract(pos).normalize()
    verticalAngle = Math.asin(-direction.y).toFloat
    horizontalAngle = Math.atan2(-direction.x, -direction.z).toFloat
    normalizeAngles()
  }

  def viewportAspectRatio(): Float = {
    aspectRatio
  }

  def setAspectRatio(width: Float, height:Float): Unit = {
    val ar: Float = width / height
    println("ar: " + ar)
    assert(ar > 0.0f)
    aspectRatio = ar
  }

  def forward(): Vector3f = {
    val invertedOrientation = Matrix4f.invert(orientation())
    val forward = Matrix4f.multiply(invertedOrientation, Vector4f(0, 0, -1, 1))
    Vector3f(forward.x, forward.y, forward.z)
  }

  def right(): Vector3f = {
    val invertedOrientation = Matrix4f.invert(orientation())
    val right = Matrix4f.multiply(invertedOrientation, Vector4f(1, 0, 0, 1))
    Vector3f(right.x, right.y, right.z)
  }

  def up(): Vector3f = {
    val up = Matrix4f.multiply(Matrix4f.invert(orientation()), Vector4f(0, 1, 0, 1))
    Vector3f(up.x, up.y, up.z)
  }

  def matrix(): Matrix4f = {
    Matrix4f.multiply(projection(), view())
  }

  def projection(): Matrix4f = {
    println("AR: " + aspectRatio)
    val h = Math.tan(Math.toRadians(fov) / 2).toFloat * zNear
    val w = h * aspectRatio
    val persp = Matrix4f(zNear / w, 0.0f, 0.0f, 0.0f,
      0.0f, zNear / h, 0.0f, 0.0f,
      0.0f, 0.0f,  -(zFar + zNear) / (zFar - zNear), -1.0f,
      0.0f, 0.0f, -2.0f * zFar * zNear / (zFar - zNear), 0.0f)
    persp
  }

  def view(): Matrix4f = {
    println("pos.x: " + pos.x + " pos.y: " + pos.y + " pos.z: " + pos.z)
    Matrix4f.multiply(orientation(), Transformations.translate(-pos.x, -pos.y, -pos.z))
  }
}
