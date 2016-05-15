package net.tedstein.rope.graphics

import net.tedstein.rope.physics.RelativisticObject

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

  def updateCameraVectors(player: RelativisticObject): Unit = {
    this.position = player.pos.v
    this.cameraFront = player.front.normalize

    this.right = this.cameraFront.cross(this.worldUp).normalize  // Normalize the vectors, because their length gets closer to 0 the more you look up or down which results in slower movement.
    this.cameraUp = player.up.normalize
  }

  def lookAt(): Matrix4f = {
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

    camera
  }

}