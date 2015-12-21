package net.tedstein.rope.graphics

sealed abstract class CameraMovement
case object Forward extends CameraMovement
case object Backward extends CameraMovement
case object Left extends CameraMovement
case object Right extends CameraMovement
case object LeftYaw extends CameraMovement
case object RightYaw extends CameraMovement
