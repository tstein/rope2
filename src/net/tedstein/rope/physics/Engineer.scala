package net.tedstein.rope.physics

import com.typesafe.scalalogging.StrictLogging
import net.tedstein.rope.Universe
import net.tedstein.rope.util.Metrics

class Engineer(universe: Universe) extends Thread with StrictLogging {
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

  override def run(): Unit = {
    logger.info(s"Engineer clocking in at ${System.nanoTime}")
    shouldRun = true
    lastFrameNanos = System.nanoTime

    var targetNanos = lastFrameNanos + FrameBudgetNanos
    while (shouldRun) {
      val currentNanos = System.nanoTime
      if (lastFrameNanos + currentNanos > targetNanos) {
        val elapsed = (currentNanos - lastFrameNanos).toDouble / Billion
        update(elapsed)

        val nanosSpent = System.nanoTime - currentNanos
        Metrics.addValue(Metrics.EngineerNanosSpent, nanosSpent)

        targetNanos = currentNanos + FrameBudgetNanos
        lastFrameNanos = currentNanos
        framesEngineered += 1

        if (framesEngineered > 0 && framesEngineered % 3600 == 0) {
          val headroom = (1.0 - (Metrics.simpleAverage(Metrics.EngineerNanosSpent) / FrameBudgetNanos)) * 100
          logger.debug(f"physics headroom: $headroom%.1f%%")
        }
      }

      Thread.sleep(nanosToMillis(SleepNanos))
    }

    logger.info(s"Engineer clocking out at ${System.nanoTime}")
  }

  def shutdown(): Unit = shouldRun = false

  private def update(elapsed: Double): Unit = {
    universe.squares.par.foreach(updateObject(_, universe.player, elapsed))
  }

  private def updateObject(something: RelativisticObject, player: RelativisticObject, elapsed: Double): Unit = {
    // For starters, let's just update positions based on local velocity.
    something.pos = something.pos.add(something.vel, elapsed)
  }

  private def nanosToMillis(nanos: Long): Long = nanos / Million
}
