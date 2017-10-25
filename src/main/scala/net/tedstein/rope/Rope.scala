package net.tedstein.rope

import net.tedstein.rope.physics.Engineer
import net.tedstein.rope.universe.Universe


object Rope {
  def main(args: Array[String]) = {
    System.loadLibrary("rope")

    // Let there be light.
    val universe = Universe.demo

    Graphics.start()

    val engineer = new Engineer(universe)
    engineer.run()
  }
}
