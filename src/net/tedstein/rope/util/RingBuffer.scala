package net.tedstein.rope.util

class RingBuffer(val size: Int) {
  private var _values = RingBuffer.initValues(size)
  private var _nextInsert = 0

  def insert(value: Double): Unit = {
    _values(_nextInsert) = Some(value)
    _nextInsert = (_nextInsert + 1) % size
  }

  def clear(): Unit = {
    _values = RingBuffer.initValues(size)
  }

  def sum: Double = {
    _values.flatten.sum
  }

  def simpleAverage: Double = {
    _values.flatten.length match {
      case 0 => 0.0
      case definedValues => sum / definedValues
    }
  }
}

object RingBuffer {
  private def initValues(size: Int): Array[Option[Double]] = {
    (0 to size).map { x => None }.toArray
  }
}