package net.tedstein.rope.graphics

object Transformations {
  /* Create translation matrix */

  def translate(m: Matrix4f, translation: Vector3f): Matrix4f = {
    val v0 = Vector4f(m.matrix(0)(0) * translation.x, m.matrix(0)(1) * translation.x,
                      m.matrix(0)(2) * translation.x, m.matrix(0)(3) * translation.x)

    val v1 = Vector4f(m.matrix(1)(0) * translation.y, m.matrix(1)(1) * translation.y,
      m.matrix(1)(2) * translation.y, m.matrix(1)(3) * translation.y)

    val v2 = Vector4f(m.matrix(2)(0) * translation.z, m.matrix(2)(1) * translation.z,
      m.matrix(2)(2) * translation.z, m.matrix(2)(3) * translation.z)

    val v3 = Vector4f(m.matrix(3)(0), m.matrix(3)(1), m.matrix(3)(2), m.matrix(3)(3))
    val res = v0.add(v1).add(v2).add(v3)

    val transMatrix = Matrix4f()
    transMatrix.matrix(0)(0) = m.matrix(0)(0)
    transMatrix.matrix(0)(1) = m.matrix(0)(1)
    transMatrix.matrix(0)(2) = m.matrix(0)(2)
    transMatrix.matrix(0)(3) = m.matrix(0)(3)

    transMatrix.matrix(1)(0) = m.matrix(1)(0)
    transMatrix.matrix(1)(1) = m.matrix(1)(1)
    transMatrix.matrix(1)(2) = m.matrix(1)(2)
    transMatrix.matrix(1)(3) = m.matrix(1)(3)

    transMatrix.matrix(2)(0) = m.matrix(2)(0)
    transMatrix.matrix(2)(1) = m.matrix(2)(1)
    transMatrix.matrix(2)(2) = m.matrix(2)(2)
    transMatrix.matrix(2)(3) = m.matrix(2)(3)

    transMatrix.matrix(3)(0) = res.x
    transMatrix.matrix(3)(1) = res.y
    transMatrix.matrix(3)(2) = res.z
    transMatrix.matrix(3)(3) = res.w
    transMatrix
  }
  def translate(transform: Matrix4f, x: Float, y: Float, z: Float): Matrix4f = {
    val translation = Matrix4f()
    translation.matrix(3)(0) = x
    translation.matrix(3)(1) = y
    translation.matrix(3)(2) = z

    translation.matrix(0)(0) = 1.0f
    translation.matrix(1)(1) = 1.0f
    translation.matrix(2)(2) = 1.0f
    translation.matrix(3)(3) = 1.0f

    Matrix4f.multiply(transform, translation)

    //translation
  }

  /* Create rotation matrix */

  def rotate(transform: Matrix4f, angleInDegrees: Float, x: Float, y: Float, z: Float) = {
    val rcos: Float = Math.cos(Math.toRadians(angleInDegrees)).toFloat
    val rsin: Float = Math.sin(Math.toRadians(angleInDegrees)).toFloat

    val rotation = Matrix4f()
    rotation.matrix(0)(0) = x * x * (1f - rcos) + rcos
    rotation.matrix(0)(1) = y * x * (1f - rcos) + z * rsin
    rotation.matrix(0)(2) = x * z * (1f - rcos) - y * rsin
    rotation.matrix(0)(3) = 0.0f

    rotation.matrix(1)(0) = x * y * (1f - rcos) - z * rsin
    rotation.matrix(1)(1) = y * y * (1f - rcos) + rcos
    rotation.matrix(1)(2) = y * z * (1f - rcos) + x * rsin
    rotation.matrix(1)(3) = 0.0f

    rotation.matrix(2)(0) = x * z * (1f - rcos) + y * rsin
    rotation.matrix(2)(1) = y * z * (1f - rcos) - x * rsin
    rotation.matrix(2)(2) = z * z * (1f - rcos) + rcos
    rotation.matrix(2)(3) = 0.0f

    rotation.matrix(3)(0) = 0.0f
    rotation.matrix(3)(1) = 0.0f
    rotation.matrix(3)(2) = 0.0f
    rotation.matrix(3)(3) = 1.0f


    Matrix4f.multiply(rotation, transform)

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
  def scale(transform: Matrix4f, x: Float, y: Float, z: Float): Matrix4f = {

    val scaling = Matrix4f()
    scaling.matrix(0)(0) = x
    scaling.matrix(1)(1) = y
    scaling.matrix(2)(2) = z
    scaling.matrix(3)(3) = 1.0f
    Matrix4f.multiply(transform, scaling)
  }

  def modelTransformation(translate: Matrix4f, rotate: Matrix4f, scale: Matrix4f): Matrix4f = {
    var transform = Matrix4f()
    transform = Matrix4f.multiply(translate, Matrix4f.multiply(rotate, scale)) //translate * rotate * scale
    transform
  }

  def perspective(fovy: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f = {
    val ar = width / height
    val h = (Math.tan(Math.toRadians(fovy) * 0.5f) * zNear).toFloat
    val w = h * ar.toFloat
    Matrix4f(zNear / w, 0.0f, 0.0f, 0.0f,
                      0.0f, zNear / h, 0.0f, 0.0f,
                      0.0f, 0.0f,  -(zFar + zNear) / (zFar - zNear), -1.0f,
                      0.0f, 0.0f, -2.0f * zFar * zNear / (zFar - zNear), 0.0f)

  }

  def ortho(left: Float, right: Float, bottom: Float, top: Float, zNear: Float, zFar: Float): Matrix4f = {
    val m00: Float = 2f / (right - left)
    val m11: Float = 2f / (top - bottom)
    val m22: Float = -2f / (zFar - zNear)
    val m30: Float = - (right + left) / (right - left)
    val m31: Float = - (top + bottom) / (top - bottom)
    val m32: Float = - (zFar + zNear) / (zFar - zNear)

    Matrix4f(m00, 0.0f, 0.0f, 0.0f,
      0.0f, m11, 0.0f, 0.0f,
      0.0f, 0.0f, m22, 0.0f,
      m30, m31, m32, 1.0f)
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
