package net.tedstein.rope.util

import scala.collection.mutable.ArrayBuffer

class Stopwatch {
  val laps = new ArrayBuffer[(Long, String)]
  lap("start")

  def lap(label: String): Unit = {
    laps.append((System.nanoTime(), label))
  }

  override def toString: String = {
    laps.tail.map { case (time, label) =>
      val millisSinceStart = (time - laps.head._1) / 1000000
      s"$label @ +$millisSinceStart ms"
    }.mkString(", ")
  }
}
