package net.tedstein.rope.graphics

case class Camera() {
  var position = Vector3f(0.0f, 0.0f, 3.0f)
  var worldUp = Vector3f(0.0f, 1.0f, 0.0f)
  var cameraUp = Vector3f.Zero
  var right = Vector3f.Zero
  var cameraFront = Vector3f(0.0f, 0.0f, -1.0f)

  var pitch = Camera.DefaultPitch
  var yaw = Camera.DefaultYaw
  var movementSpeed = Camera.DefaultSpeed
  var mouseSensitivity = Camera.DefaultSensitivity
  var zoom = Camera.DefaultZoom

  def lookAt(eye: Vector3f, target: Vector3f, camUp: Vector3f): Matrix4f = {
    val f: Vector3f = target.subtract(eye).normalize
    var u: Vector3f = camUp.normalize
    val s: Vector3f = f.cross(u).normalize
    u = s.cross(f)

    Matrix4f(s.x, u.x, -f.x, 0.0f,
             s.y, u.y, -f.y, 0.0f,
             s.z, u.z, -f.z, 0.0f,
             -s.dot(eye), -u.dot(eye), f.dot(eye), 1.0f)
  }


  def processKeyboard(direction: CameraMovement, deltaTime: Float) {
    val velocity = this.movementSpeed * deltaTime
    if (direction == Forward) {
      this.position = this.position.add(this.cameraFront.scale(velocity))
    }
    if (direction == Backward) {
      this.position = this.position.subtract(this.cameraFront.scale(velocity))
    }
    if (direction == Left) {
      this.position = this.position.subtract(this.right.scale(velocity))
    }
    if (direction == Right) {
      this.position = this.position.add(this.right.scale(velocity))
    }
    if (direction == RightYaw) {
      //for now: 30 is a magic number
      this.yaw = this.yaw + 30 * deltaTime
      updateCameraVectors
    }
    if (direction == LeftYaw) {
      this.yaw = this.yaw - 30 * deltaTime
      updateCameraVectors
    }
  }

  def updateCameraVectors: Unit = {
    // Calculate the new Front vector
    val frontx = (Math.cos(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch))).toFloat
    val fronty = Math.sin(Math.toRadians(this.pitch)).toFloat
    val frontz = (Math.sin(Math.toRadians(this.yaw)) * Math.cos(Math.toRadians(this.pitch))).toFloat
    this.cameraFront = Vector3f(frontx, fronty, frontz)
    this.cameraFront.normalize

    // Also re-calculate the Right and Up vector
    this.right = this.cameraFront.cross(this.worldUp).normalize  // Normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
    this.cameraUp = this.right.cross(this.cameraFront).normalize
  }

  def getViewMatrix(): Matrix4f = {

    lookAt(this.position, this.position.add(this.cameraFront), this.cameraUp)
  }
}

object Camera {
  val DefaultYaw = -90.0f
  val DefaultPitch = 0.0f
  val DefaultSpeed = 3.0f
  val DefaultSensitivity = 0.25f
  val DefaultZoom = 45.0f

  def apply(position: Vector3f = Vector3f.Zero, worldUp: Vector3f = Vector3f(0.0f, 1.0f, 0.0f),
            yaw: Float = Camera.DefaultYaw, pitch: Float = Camera.DefaultPitch): Camera = {

    val camera = Camera()
    camera.position = position
    camera.worldUp = worldUp
    camera.yaw = yaw
    camera.pitch = pitch
    camera.updateCameraVectors

    camera
  }
  // Constructor with scalar values
  def apply(posX: Float, posY: Float, posZ: Float, upX: Float, upY: Float, upZ: Float, yaw: Float, pitch: Float
             ): Camera = {
    val camera = Camera()
    camera.position = Vector3f(posX, posY, posZ)
    camera.worldUp = Vector3f(upX, upY, upZ)
    camera.yaw = yaw
    camera.pitch = pitch
    camera.updateCameraVectors

    camera
  }

}