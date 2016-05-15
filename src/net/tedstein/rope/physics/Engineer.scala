package net.tedstein.rope.physics

import com.typesafe.scalalogging.StrictLogging
import net.tedstein.rope.Universe
import net.tedstein.rope.physics.Dimensions.Velocity
import net.tedstein.rope.util.Metrics

class Engineer(universe: Universe) extends Thread("engineering") with StrictLogging {
  val Thousand = 1000L
  val Million = Thousand * Thousand
  val Billion = Thousand * Million

  // Sleep 1 ms between update checks.
  val SleepNanos = Million
  val TargetPhysicsFrameRate = 60
  val FrameBudgetNanos = Billion / TargetPhysicsFrameRate

  var shouldRun = false
  var lastFrameNanos = 0L
  var framesEngineered = 0L
  val playerAcceleration = 0.25 // c per second.  1 will be absurd, maybe go with this
  val playerTurnRate = 0.3 // rad/s

  override def run(): Unit = {
    logger.info(s"clocking in at ${System.nanoTime}")
    shouldRun = true
    lastFrameNanos = System.nanoTime

    var nextFrameNanos = lastFrameNanos + FrameBudgetNanos
    while (shouldRun) {
      val currentNanos = System.nanoTime
      if (lastFrameNanos + currentNanos > nextFrameNanos) {


        val elapsed = (currentNanos - lastFrameNanos).toDouble / Billion
        updateEverything(elapsed)
        val nanosSpent = System.nanoTime - currentNanos
        Metrics.addValue(Metrics.EngineerNanosSpent, nanosSpent)


        nextFrameNanos = currentNanos + FrameBudgetNanos
        lastFrameNanos = currentNanos

        framesEngineered += 1
        if (framesEngineered > 0 && framesEngineered % 3600 == 0) {
          val headroom = (1.0 - (Metrics.simpleAverage(Metrics.EngineerNanosSpent) / FrameBudgetNanos)) * 100
          logger.debug(f"physics headroom: $headroom%.1f%%")
        }
      }

      Thread.sleep(nanosToMillis(SleepNanos))
    }

    logger.info(s"clocking out at ${System.nanoTime}")
  }

  def shutdown(): Unit = shouldRun = false

  private def updateEverything(elapsed: Double): Unit = {
    updatePlayer(universe.player, elapsed)
    universe.bodies.par.foreach(updateObject(_, universe.player, elapsed))
    //universe.bodies.par.foreach(updateObjectRelativistic(_, universe.player, elapsed))
  }

  private def updatePlayer(player: RelativisticObject, elapsed: Double): Unit = {
    // Flags indicating which input keys are currently pressed.
    var forward, backward = false
    var strafeLeft, strafeRight = false
    var yawLeft, yawRight = false
    var rise, fall = false
    var slowDown = false

    Input.synchronized {
      forward = Input.forward
      backward = Input.backward
      strafeLeft = Input.strafeLeft
      strafeRight = Input.strafeRight
      yawLeft = Input.yawLeft
      yawRight = Input.yawRight
      rise = Input.rise
      fall = Input.fall
      slowDown = Input.slowDown
    }
    //println(s"forward = $forward, backward = $backward, strafeLeft = $strafeLeft, strafeRight = $strafeRight, yawLeft = $yawLeft, yawRight = $yawRight, rise = $rise, fall = $fall, slowDown = $slowDown")

    //Quick hack
    //Direction first
    if (yawLeft) {
      player.front = player.front.rotate(-playerTurnRate * elapsed, player.up)
    }
    if (yawRight) {
      player.front = player.front.rotate(playerTurnRate * elapsed, player.up)
    }
    //TODO: pitch, roll

    player.front = player.front.normalize
    player.up = player.up.normalize
    val right = player.front.cross(player.up)

    //Velocity changes

    if (forward) {
      player.vel = player.vel.unBoost(Velocity(player.front * playerAcceleration * elapsed))
    }
    if (backward) {
      player.vel = player.vel.unBoost(Velocity(player.front * playerAcceleration * elapsed * (-1)))
    }

    if (strafeLeft) {
      player.vel = player.vel.unBoost(Velocity(right * playerAcceleration * elapsed * (-1)))
    }
    if (strafeRight) {
      player.vel = player.vel.unBoost(Velocity(right * playerAcceleration * elapsed))
    }
    if (rise) {
      player.vel = player.vel.unBoost(Velocity(player.up * playerAcceleration * elapsed))
    }
    if (fall) {
      player.vel = player.vel.unBoost(Velocity(player.up * playerAcceleration * elapsed * (-1)))
    }
    if (slowDown) {
      player.vel = Dimensions.Stationary
    }

    //Position changes

    player.pos = player.pos.drift(player.vel, elapsed)

    //println(s"player.pos = ${player.pos}, player.vel = ${player.vel}, player.front = ${player.front}")

  }

  private def updateObject(something: RelativisticObject, player: RelativisticObject, elapsed: Double): Unit = {
    something match {
      case orbiter: SimpleOrbiter =>
        orbiter.time += elapsed
        orbiter.pos = orbiter.computedPosition
        orbiter.vel = orbiter.computedVelocity
      case orbiter2: Orbiter =>
        orbiter2.updatePositionAndVelocity(orbiter2.time + elapsed)
    }
  }
  private def updateObjectRelativistic(something: RelativisticObject, player: RelativisticObject, elapsed: Double): Unit = {
    //TODO: Good test for this function
    //Clumsy (possibly have the wrong v, no approximation error correction) Newton's method.
    //Upgrade to Range-Kutta?  Should be more stable and accurate, though it does the calculation 4 times.
    //Upgrade to correct for absolute time drift?

    //Get Vob - Vpl component in the direction away from the player
    val velrssFromPlayer = something.vel.boost(player.vel).v * (something.pos.v - player.pos.v).normalize
    val timeAdd = elapsed / (1 + velrssFromPlayer)

    something match {
      case orbiter: SimpleOrbiter =>
        orbiter.time += timeAdd
        orbiter.pos = orbiter.computedPosition
        orbiter.vel = orbiter.computedVelocity
      case orbiter2: Orbiter =>
        orbiter2.updatePositionAndVelocity(orbiter2.time + timeAdd)
    }
  }

  private def nanosToMillis(nanos: Long): Long = nanos / Million
}
