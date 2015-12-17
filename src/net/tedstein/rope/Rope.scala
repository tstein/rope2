package net.tedstein.rope

import net.tedstein.rope.Dimensions.Position


object Rope {
  def main(args: Array[String]) = {
    val sphere = RelativisticObject(Dimensions.Origin, Dimensions.Stationary, 0.0)
    val cubes = (for (i <- -1 to 1; j <- -1 to 1; k <- -1 to 1)
      yield RelativisticObject(Position(i.toDouble, j.toDouble, k.toDouble), Dimensions.Stationary, 0.0)
      ).toSet
    // Let there be light.
    val universe = new Universe(sphere, cubes)

    val graphics = new Graphics(universe)
    graphics.run()

    println("Up and running!")
  }
}
