package net.tedstein.rope.graphics

import com.typesafe.scalalogging.StrictLogging

import scala.io.Source
/**
  * Created by ruba on 1/16/16.
  */

object OBJLoader extends StrictLogging {
  def parseObjFile(filename: String): Unit = {
  val model = Mesh()
    try {
      for (line <- Source.fromFile(filename).getLines()) {
        val values = line.split(" ")
        //println(values(0))
        if (values(0) == "#") {
        } else if (values(0) == "v") {
            model.vertices ++= extractVertices(values.tail)
        } else if (values(0) == "vn") {
            model.normals ++= extractVertices(values.tail)
        } else if (values(0) == "vt") {
            model.texCoords ++= extractVertices(values.tail)
        } else if (values(0) == "f") {
            //model.faces ++= extractVertices(values.tail)
        }
      }

    println("verts: " + model.vertices)
    } catch {
      case ex: Exception => throw ex
    }
  }

  def extractVertices(values: Array[String]): Array[Float]= {
     values.map(v => v.toFloat)
  }

  def extractNormals(values: Array[String]): Array[Float] = {
    values.map(v => v.toFloat)
  }

  def extractTexCoords(values: Array[String]): Array[Float] = {
    values.map(v => v.toFloat)
  }

  def extractFaces(values: Array[String]): Array[Int] = {
    //line looks like this:
    //#f v1/vn1/vt1
    //
    values.map(v => v.toInt)

  }

}
