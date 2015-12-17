package net.tedstein.rope

object Transformations {
  /* Create translation matrix */
  def translate(x: Float, y: Float, z: Float): Matrix4f = {
    val translation = Matrix4f()
    translation.matrix(3)(0) = x
    translation.matrix(3)(1) = y
    translation.matrix(3)(2) = z

    translation.matrix(0)(0) = 1.0f
    translation.matrix(1)(1) = 1.0f
    translation.matrix(2)(2) = 1.0f
    translation.matrix(3)(3) = 1.0f


    translation

  }

  /* Create rotation matrix */

  def rotate(rotation: Matrix4f, angleInDegrees: Float, x: Float, y: Float, z: Float): Matrix4f = {
    val rcos: Float = Math.cos(Math.toRadians(angleInDegrees)).toFloat
    val rsin: Float = Math.sin(Math.toRadians(angleInDegrees)).toFloat

    rotation.matrix(0)(0) = x * x * (1f - rcos) + rcos
    rotation.matrix(0)(1) = y * x * (1f - rcos) + z * rsin
    rotation.matrix(0)(2) = -y * rsin + z * x * (1 - rcos)
    rotation.matrix(0)(3) = 0.0f

    rotation.matrix(1)(0) = -z * rsin + x * y * (1 - rcos)
    rotation.matrix(1)(1) = rcos + y * y * (1 - rcos)
    rotation.matrix(1)(2) = -y * rsin + z * x * (1 - rcos)
    rotation.matrix(1)(3) = 0.0f

    rotation.matrix(2)(0) = y * rsin + x * z * (1 - rcos)
    rotation.matrix(2)(1) = -x * rsin + y * z * (1 - rcos)
    rotation.matrix(2)(2) = rcos + z * z * (1 - rcos)
    rotation.matrix(2)(3) = 0.0f

    rotation.matrix(3)(0) = 0.0f
    rotation.matrix(3)(1) = 0.0f
    rotation.matrix(3)(2) = 0.0f
    rotation.matrix(3)(3) = 1.0f

    rotation
  }

  def rotate(angleX: Float, angleY: Float, angleZ: Float): Matrix4f = {
    val x: Float = Math.toRadians(angleX).toFloat
    val y: Float = Math.toRadians(angleY).toFloat
    val z: Float = Math.toRadians(angleZ).toFloat

    val rotX = Matrix4f(1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, Math.cos(x).toFloat, -Math.sin(x).toFloat, 0.0f,
                        0.0f, Math.sin(x).toFloat, Math.cos(x).toFloat, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f)

    val rotY = Matrix4f(Math.cos(y).toFloat, 0.0f, -Math.sin(y).toFloat, 0.0f,
                        0.0f, 1.0f, 0.0f, 0.0f,
                        Math.sin(y).toFloat, 0.0f, Math.cos(y).toFloat, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f)

    val rotZ = Matrix4f(Math.cos(z).toFloat, -Math.sin(z).toFloat, 0.0f, 0.0f,
                        Math.sin(z).toFloat, Math.cos(z).toFloat, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f)

    Matrix4f.multiply(rotZ, Matrix4f.multiply(rotY, rotX))
  }



  /* Create scaling matrix */
  def scale(x: Float, y: Float, z: Float): Matrix4f = {
    val scaling = Matrix4f()
    scaling.matrix(0)(0) = x
    scaling.matrix(1)(1) = y
    scaling.matrix(2)(2) = z
    scaling.matrix(3)(3) = 1.0f
    scaling
  }

  def getModelTransformation(translate: Matrix4f, rotate: Matrix4f, scale: Matrix4f): Matrix4f = {
    var transform = Matrix4f()
    transform = Matrix4f.multiply(translate, Matrix4f.multiply(rotate, scale)) //translate * rotate * scale
    transform
  }

  def getProjectionTransformation(fovy: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f = {
    println("W/H " + width/height)
    val h = Math.tan(Math.toRadians(fovy) / 2).toFloat * zNear
    val w = h * width / height
    val persp = Matrix4f(zNear / w, 0.0f, 0.0f, 0.0f,
                      0.0f, zNear / h, 0.0f, 0.0f,
                      0.0f, 0.0f,  -(zFar + zNear) / (zFar - zNear), -1.0f,
                      0.0f, 0.0f, -2.0f * zFar * zNear / (zFar - zNear), 0.0f)

    persp
  }

  def getViewTransformation(eye: Vector3f, center: Vector3f, up: Vector3f): Matrix4f = {
    val f: Vector3f = center.subtract(eye).normalize
    var u = up.normalize
    val s = f.cross(u).normalize
    u = s.cross(f)

    Matrix4f(
        s.x, u.x, -f.x, 0.0f,
        s.y, u.y, -f.y, 0.0f,
        s.z, u.z, -f.z, 0.0f,
        -s.dot(eye), -u.dot(eye), f.dot(eye), 1.0f
      )
    }


}
