package net.tedstein.rope.physics

import net.tedstein.rope.Universe

class Engineer(universe: Universe) extends Thread {
  val Thousand = 1000L
  val Million = Thousand * Thousand
  val Billion = Thousand * Million
  var shouldRun = false
  var lastFrameNanos = 0L

  // Sleep 1 ms between update checks.
  val SleepNanos = Million
  val TargetPhysicsFrameRate = 60
  val TimeResolutionNanos = Billion / TargetPhysicsFrameRate

  override def run(): Unit = {
    println(s"Engineer clocking in at ${System.nanoTime}")
    shouldRun = true
    lastFrameNanos = System.nanoTime

    var targetNanos = lastFrameNanos + TimeResolutionNanos
    while (shouldRun) {
      val currentNanos = System.nanoTime
      if (lastFrameNanos + currentNanos > targetNanos) {
        val elapsed = (currentNanos - lastFrameNanos).toDouble / Billion
        update(elapsed)

        targetNanos = currentNanos + TimeResolutionNanos
        lastFrameNanos = currentNanos
      }

      Thread.sleep(nanosToMillis(SleepNanos))
    }

    println(s"Engineer clocking out at ${System.nanoTime}")
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
