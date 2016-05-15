package net.tedstein.rope.physics

object Input {
  var forward, backward = false
  var strafeLeft, strafeRight = false
  var yawLeft, yawRight = false
  var rise, fall = false

  def update(
              forward: Boolean, backward: Boolean,
              strafeLeft: Boolean, strafeRight: Boolean,
              yawLeft: Boolean, yawRight: Boolean,
              rise: Boolean, fall: Boolean): Unit = {
    Input.synchronized {
      this.forward = forward
      this.backward = backward
      this.strafeLeft = strafeLeft
      this.strafeRight = strafeRight
      this.yawLeft = yawLeft
      this.yawRight = yawRight
      this.rise = rise
      this.fall = fall
    }
  }
}
