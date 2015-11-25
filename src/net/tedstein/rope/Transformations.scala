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
  //not good

  def rotate(angleInDegrees: Float, x: Float, y: Float, z: Float): Matrix4f = {
    val rotation = Matrix4f()
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


    /* a better way!:
    Matrix4f rx, ry, rz;

    const float x = ToRadian(RotateX);
    const float y = ToRadian(RotateY);
    const float z = ToRadian(RotateZ);

    rx.m[0][0] = 1.0f; rx.m[0][1] = 0.0f   ; rx.m[0][2] = 0.0f    ; rx.m[0][3] = 0.0f;
    rx.m[1][0] = 0.0f; rx.m[1][1] = cosf(x); rx.m[1][2] = -sinf(x); rx.m[1][3] = 0.0f;
    rx.m[2][0] = 0.0f; rx.m[2][1] = sinf(x); rx.m[2][2] = cosf(x) ; rx.m[2][3] = 0.0f;
    rx.m[3][0] = 0.0f; rx.m[3][1] = 0.0f   ; rx.m[3][2] = 0.0f    ; rx.m[3][3] = 1.0f;

    ry.m[0][0] = cosf(y); ry.m[0][1] = 0.0f; ry.m[0][2] = -sinf(y); ry.m[0][3] = 0.0f;
    ry.m[1][0] = 0.0f   ; ry.m[1][1] = 1.0f; ry.m[1][2] = 0.0f    ; ry.m[1][3] = 0.0f;
    ry.m[2][0] = sinf(y); ry.m[2][1] = 0.0f; ry.m[2][2] = cosf(y) ; ry.m[2][3] = 0.0f;
    ry.m[3][0] = 0.0f   ; ry.m[3][1] = 0.0f; ry.m[3][2] = 0.0f    ; ry.m[3][3] = 1.0f;

    rz.m[0][0] = cosf(z); rz.m[0][1] = -sinf(z); rz.m[0][2] = 0.0f; rz.m[0][3] = 0.0f;
    rz.m[1][0] = sinf(z); rz.m[1][1] = cosf(z) ; rz.m[1][2] = 0.0f; rz.m[1][3] = 0.0f;
    rz.m[2][0] = 0.0f   ; rz.m[2][1] = 0.0f    ; rz.m[2][2] = 1.0f; rz.m[2][3] = 0.0f;
    rz.m[3][0] = 0.0f   ; rz.m[3][1] = 0.0f    ; rz.m[3][2] = 0.0f; rz.m[3][3] = 1.0f;

    *this = rz * ry * rx;                       */
    rotation
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

  def getModelTransformMatrix(translate: Matrix4f, rotate: Matrix4f, scale: Matrix4f): Matrix4f = {
    var transform = Matrix4f()
    transform = Matrix4f.multiply(translate, Matrix4f.multiply(rotate, scale)) //translate * rotate * scale
    transform
  }

  def setPerspectiveProjection(fovy: Float, width: Float, height: Float, zNear: Float, zFar: Float): Matrix4f = {
    val halfFovyRadians: Float = Math.toRadians(fovy / 2).toFloat
    val range = zNear - zFar
    val aspectRatio = width / height

    val persp = Matrix4f(1.0f / (halfFovyRadians * aspectRatio), 0.0f, 0.0f, 0.0f,
      0.0f, 1.0f / halfFovyRadians, 0.0f, 0.0f,
      0.0f, 0.0f, -(zNear + zFar) / range, -2.0f * zFar * zNear / range,
      0.0f, 0.0f, -1.0f, 0.0f)
    persp
  }

  def getWorldPerspectiveTransformation(persp: Matrix4f, world: Matrix4f): Matrix4f = {
    Matrix4f.multiply(persp, world)
  }
}
