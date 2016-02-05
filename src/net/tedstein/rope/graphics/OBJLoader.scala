package net.tedstein.rope.graphics

import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object OBJLoader extends StrictLogging {
  def parseObjFile(filename: String, model: Mesh): Unit = {

    var unorderedVertices = List[Float]()
    var unorderedTexes = List[Float]()
    try {
      for (line <- Source.fromFile(filename).getLines()) {
        val prefix = line.split(" ")(0)
        if (prefix == "#") {
        } else if (prefix == "v") {
            unorderedVertices ++= extractVertices(line)
        } else if (prefix == "vn") {
            model.normals ++= extractNormals(line)
        } else if (prefix == "vt") {
            unorderedTexes ++= extractTexCoords(line)
        } else if (prefix == "f") {
            model.vertIndecies ++= extractVertexIndecies(line)
            model.texIndecies ++= extractTexCoordIndecies(line)
            if (model.hasNormals) {
              model.normalIndecies ++= extractNormalsIndecies(line)
            }
        }
      }

      model.vertices ++= unorderedVertices
      model.texCoords ++= reorderTexes(unorderedTexes, model.texIndecies)
    } catch {
      case ex: Exception => throw ex
    }
  }


  def reorderTexes(unorderedTexes: List[Float], indecies: List[Int]): List[Float] = {
    val orderedTexes = new Array[Float](indecies.length * 2)
    for (i <- 0 to unorderedTexes.length) {
      val correctIndex = indecies(i) - 1
      orderedTexes.update(2 * i, unorderedTexes(2 * correctIndex))
      orderedTexes.update(2 * i + 1, unorderedTexes(2 * correctIndex + 1))
    }
    orderedTexes.toList
  }
  def reorderVertices(unorderedVertices: List[Float], indecies: List[Int]): List[Float] = {
    val orderedVertices = new Array[Float](indecies.length * 3)
    //(e, f, a, b, c, d)
    //(3, 1, 2)
    for (i <- 0 to unorderedVertices.length) {
      val correctIndex = indecies(i) - 1
      orderedVertices.update(3 * i, unorderedVertices(3 * correctIndex))
      orderedVertices.update(3 * i + 1, unorderedVertices(3 * correctIndex + 1))
      orderedVertices.update(3 * i + 2, unorderedVertices(3 * correctIndex + 2))
    }
    orderedVertices.toList
  }

  def extractVertices(line: String): List[Float]= {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(x, y, z)
  }

  def graphicsFlatten(verts: List[Float], texes: List[Float]): List[Float] = {

    var output = ArrayBuffer[Float]()
    for (i <- 0 to (verts.length / 3) - 1) {
      output += verts(3 * i ) //0
      output += verts(3 * i + 1) //1
      output += verts(3 * i + 2) //2

      output += texes(2 * i + 0) //0
      output += texes(2 * i + 1) //1
    }

    output.toList
  }



  def extractNormals(line: String): List[Float] = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(x, y , z)

  }

  def extractTexCoords(line: String): List[Float] = {
    //line looks like this:
    //#vt 0.999896 0.983649
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    List(x , y)
  }

  def extractVertexIndecies(line: String): Array[Int] = {
    //line looks like this:
    //#f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
    //we want (v1, v2, v3)
    //values.map(v => v.split("/")(0).toInt)
    val v1 = line.split(" ")(1).split("/")(0).toInt
    val v2 = line.split(" ")(2).split("/")(0).toInt
    val v3 = line.split(" ")(3).split("/")(0).toInt
    Array(v1 - 1, v2 - 1, v3 - 1)
  }

  def extractTexCoordIndecies(line: String): Array[Int] = {
    //line looks like this:
    //#f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
    //we want (vt1, vt2, vt3)
    val vt1 = line.split(" ")(1).split("/")(1).toInt
    val vt2 = line.split(" ")(2).split("/")(1).toInt
    val vt3 = line.split(" ")(3).split("/")(1).toInt
    Array(vt1, vt2, vt3)
  }

  def extractNormalsIndecies(line: String): Array[Int] = {
    //line looks like this:
    //#f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
    //we want (vn1, vn2, vn3)
  //  values.map(v => v.split("/")(1).toInt)
    val vn1 = line.split(" ")(1).split("/")(2).toInt
    val vn2 = line.split(" ")(2).split("/")(2).toInt
    val vn3 = line.split(" ")(3).split("/")(2).toInt
    Array(vn1, vn2, vn3)
  }


}
