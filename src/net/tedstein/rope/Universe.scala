package net.tedstein.rope

import net.tedstein.rope.physics.{RelativisticObject, Dimensions}
import Dimensions.{Velocity, Position}


class Universe(val player: RelativisticObject, val squares: Set[RelativisticObject])

object Universe {
  def demo: Universe = {
    val player = RelativisticObject(Dimensions.Origin, Dimensions.Stationary, 0.0)
    val squares = (for (i <- -1 to 1; j <- -1 to 1; k <- -1 to 1)
      yield RelativisticObject(
        Position(i.toDouble, j.toDouble, k.toDouble),
        Velocity(i * .02, j * .02, k * .02),
        0.0)
      ).toSet
    new Universe(player, squares)
  }
}