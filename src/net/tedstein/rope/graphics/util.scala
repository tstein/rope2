package net.tedstein.rope.graphics

import java.nio.{FloatBuffer, IntBuffer}

object util {
  def printFloatBuffer(buff: FloatBuffer, len: Int): String = {
    val s = new StringBuilder
    s.append("{")
    for (i <- 0 to len - 1) {
      if (i % 4 == 0 && i > 0) {
        s.append {
          "\n "}
      }
        s.append(s" ${buff.get(i)}, ")
    }
    s.append("}")
    s.toString()
  }

  def printIntBuffer(buff: IntBuffer, len: Int): String = {
    val s = new StringBuilder
    s.append("\n{")
    for (i <- 0 to len - 1){
      if (i % 5 == 0 && i > 0) {
        s.append {"\n "}
      }
      s.append(s" ${buff.get(i)}, ")

    }
    s.append("}")
    s.toString()
  }
}
