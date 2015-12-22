package net.tedstein.rope.util

import scala.collection.mutable

object Metrics {
  // metric names - keep 'em here
  val EngineerNanosSpent = "EngineerNanosSpent"

  private val RingSize = 600
  private val metrics = mutable.Map[String, RingBuffer]()

  def addValue(metric: String, value: Double): Unit = {
    metrics.get(metric) match {
      case None =>
        val ring = new RingBuffer(RingSize)
        metrics.put(metric, ring)
        ring.insert(value)
      case Some(ring) =>
        ring.insert(value)

    }
    metrics(metric).insert(value)
  }

  def sum(metric: String): Double = {
    metrics(metric).sum
  }

  def simpleAverage(metric: String): Double = {
    metrics(metric).simpleAverage
  }
}
