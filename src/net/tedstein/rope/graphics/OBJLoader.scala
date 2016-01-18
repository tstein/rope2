package net.tedstein.rope.graphics

import com.typesafe.scalalogging.StrictLogging
import scala.io.Source
/**
  * Created by ruba on 1/16/16.
  */

object OBJLoader extends StrictLogging {
  def parseObjFile(filename: String): Unit = {
    val model = Model()

    try {
      for (line <- Source.fromFile(filename).getLines()) {
        val words = line.split(" ")
        println(words(0))
        if (words(0) == "#") {
        } else if (words(0) == "v") {
          println("v")
          model.vertices = extractVertices(words.tail)
        } else if (words(0) == "vn") {
          model.normals = extractNormals(words.tail)
        } else if (words(0) == "vt") {
          model.texCoords = extractTexCoords(words.tail)
        } else if (words(0) == "f") {
          model.faces = extractFaces(words.tail)
        }
      }
    } catch {
      case ex: Exception => throw ex
    }
  }

  def extractVertices(words: Array[String]): Array[Float] = {
    val verts = Array[Float](words.length)
    val i = 0
    for (word <- words) {
      verts.update(i, word.toFloat)
    }
    verts
  }

  def extractNormals(words: Array[String]): Array[Float] = {
    val normals = Array[Float](words.length)
    val i = 0
    for (word <- words) {
      normals.update(i, word.toFloat)

    }
    normals
  }

  def extractTexCoords(words: Array[String]): Array[Float] = {
    val texCoords = Array[Float](words.length)
    val i = 0
    for (word <- words) {
      texCoords.update(i, word.toFloat)
    }
    texCoords
  }

  def extractFaces(words: Array[String]): Array[Int] = {
    val faces = Array[Int](words.length)
    val i = 0
    for (word <- words) {
      faces.update(i, word.toInt)
    }
    faces
  }

}
