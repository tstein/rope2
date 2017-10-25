package net.tedstein.rope.util

import org.scalatest.FunSuite

class RingBufferSuite extends FunSuite {
  test("a RingBuffer of size 0 has a sum and simple average of 0") {
    val nil = new RingBuffer(0)
    assert(nil.sum == 0)
    assert(nil.simpleAverage == 0)
  }

  test("a newly-created RingBuffer has a sum and simple average of 0") {
    val empty = new RingBuffer(10)
    assert(empty.sum == 0)
    assert(empty.simpleAverage == 0)
  }

  test("a half-full RingBuffer has a correct sum and simple average") {
    val halfFull = new RingBuffer(10)
    for (i <- 0 to 4) halfFull.insert(i)
    assert(halfFull.sum == 10)
    assert(halfFull.simpleAverage == 2)
  }

  test("a full RingBuffer has a correct sum and simple average") {
    val full = new RingBuffer(10)
    for (i <- 0 to 9) full.insert(i)
    assert(full.sum == 45)
    assert(full.simpleAverage == 4.5)
  }

  test("a twice-full RingBuffer has a correct sum and simple average") {
    val twiceFull = new RingBuffer(10)
    for (i <- 0 to 19) twiceFull.insert(i)
    assert(twiceFull.sum == 145)
    assert(twiceFull.simpleAverage == 14.5)
  }

  test("a just-cleared RingBuffer has a sum and simple average of 0") {
    val emptied = new RingBuffer(10)
    for (i <- 0 to 19) emptied.insert(i)
    emptied.clear()
    assert(emptied.sum == 0)
    assert(emptied.simpleAverage == 0)
  }
}