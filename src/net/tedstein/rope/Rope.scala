package net.tedstein.rope

import net.tedstein.rope.graphics.Graphics
import net.tedstein.rope.physics.Engineer
import net.tedstein.rope.universe.Universe


object Rope {
  def main(args: Array[String]) = {
    // Let there be light.
    val universe = Universe.demo

    val engineer = new Engineer(universe)
    engineer.start()

    val graphics = new Graphics(universe)
    // Block on the graphics loop.
    graphics.run()

    // Regardless of the reason, if graphics.run() returns, we need to shut down.
    engineer.shutdown()
  }
}
