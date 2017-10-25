package net.tedstein.rope.util

import scala.collection.mutable.ArrayBuffer

class Stopwatch(val name: String) {
  val laps = new ArrayBuffer[(Long, String)]
  lap("start")

  def lap(label: String): Unit = {
    laps.append((System.nanoTime(), label))
  }

  override def toString: String = {
    s"Stopwatch($name): " + laps.zip(laps.tail).map { case ((lastLapTime, _), (time, label)) =>
      val millisSinceStart = (time - laps.head._1) / 1000000
      val millisSinceLastLap = (time - lastLapTime) / 1000000
      s"$label @ $millisSinceStart ms (+$millisSinceLastLap)"
    }.mkString(", ")
  }
}
