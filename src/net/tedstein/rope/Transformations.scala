package net.tedstein.rope

object Transformations {
  /* Create translation matrix */
  def translate(x: Float, y :Float, z: Float ): Matrix4f = {
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

  def setPerspectiveProjection(fovy: Float, aspectRatio: Float, zNear: Float, zFar: Float): Matrix4f = {
    val halfFovyRadians: Float = Math.toRadians(fovy / 2.0f).toFloat
    val range: Float = Math.tan(halfFovyRadians).toFloat * zNear
    val left: Float = -range * aspectRatio
    val right: Float = range * aspectRatio
    val bottom: Float = -range
    val top:Float  = range
    val persective = Matrix4f((2f * zNear) / (right - left), 0f, 0f, 0f,
      0f, (2f * zNear) / (top - bottom), 0f, 0f,
      0f, 0f, -(zFar + zNear) / (zFar - zNear), -1f,
      0f, 0f, -(2f * zFar * zNear) / (zFar - zNear), 0f)

    persective
  }
  /*
  * /**
	 * Creates a perspective projection matrix using field-of-view and
	 * aspect ratio to determine the left, right, top, bottom planes.  This
	 * method is analogous to the now deprecated {@code gluPerspective} method.
	 *
	 * @param fovy field of view angle, in degrees, in the {@code y} direction
	 * @param aspect aspect ratio that determines the field of view in the x
	 * direction.  The aspect ratio is the ratio of {@code x} (width) to
	 * {@code y} (height).
	 * @param zNear near plane distance from the viewer to the near clipping plane (always positive)
	 * @param zFar far plane distance from the viewer to the far clipping plane (always positive)
	 * @return
	 */
	public static final Mat4 perspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		final float halfFovyRadians = (float) FastMath.toRadians( (fovy / 2.0f) );
		final float range = (float) FastMath.tan(halfFovyRadians) * zNear;
		final float left = -range * aspect;
		final float right = range * aspect;
		final float bottom = -range;
		final float top = range;

		return new Mat4(
				(2f * zNear) / (right - left), 0f, 0f, 0f,
				0f, (2f * zNear) / (top - bottom), 0f, 0f,
				0f, 0f, -(zFar + zNear) / (zFar - zNear), -1f,
				0f, 0f, -(2f * zFar * zNear) / (zFar - zNear), 0f
		);
	}
  * */
}
