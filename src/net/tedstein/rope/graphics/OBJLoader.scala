package net.tedstein.rope.graphics

import com.typesafe.scalalogging.StrictLogging

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object OBJLoader extends StrictLogging {
  var maxx = 0.0f
  var maxy = 0.0f
  var maxz = 0.0f
  var minx = 0.0f
  var miny = 0.0f
  var minz = 0.0f

  def parseObjFile(filename: String, model: Mesh): Unit = {
    var unorderedVertices = List[Float]()
    var unorderedTexes = List[Float]()
    for (line <- Source.fromFile(filename).getLines()) {
      val prefix = line.split(" ")(0)
      if (prefix == "v") {
        updateMaxMin(line)
        unorderedVertices ++= extractVertices(line)
      } else if (prefix == "vt") {
        unorderedTexes ++= extractTexCoords(line)
      } else if (prefix == "f") {
        model.vertIndices ++= extractVertexIndecies(line)
        model.texIndices ++= extractTexCoordIndecies(line)
      }
    }

    val avgx = (maxx + minx) / 2
    val avgy = (maxy + miny) / 2
    val avgz = (maxz + minz) / 2

    model.vertices ++= normalizeValues(unorderedVertices, avgx, avgy, avgz)
    model.texCoords ++= unorderedTexes

    val (packedverts, indices) = pack(model.vertices, model.texCoords, model.vertIndices, model.texIndices)
    model.packedverts ++= packedverts
    model.eboIndices ++= indices
  }

  def pack(verts: List[Float], texes: List[Float], vIndices: List[Int], tIndices: List[Int]): (List[Float], List[Int]) = {
    val packed = mutable.LinkedHashSet[Array[Float]]()
    val newIndices = ArrayBuffer[Int]()
    for (i <- vIndices.indices) {
      val squashed = new Array[Float](5)
      squashed.update(0, verts(3 * vIndices(i)))
      squashed.update(1, verts(3 * vIndices(i) + 1))
      squashed.update(2, verts(3 * vIndices(i) + 2))
      squashed.update(3, texes(2 * tIndices(i)))
      squashed.update(4, texes((2 * tIndices(i)) + 1))
      if (packed.contains(squashed)) {
        newIndices.append(i)
      } else {
        packed.add(squashed)
        newIndices.append(i)
      }
    }
    (packed.toList.flatten, newIndices.toList)
  }

  def normalizeTextures(texes: List[Float]): List[Float] = {
    println("texes: " + texes)
    val normalized = new Array[Float](texes.length)
    for (i <- 0 until texes.length / 2) {
      normalized(2 * i) = texes(2 * i)
      normalized((2 * i) + 1) = 1 - texes((2 * i) + 1)
    }
    println(normalized.toList)
    normalized.toList
  }

  def normalizeValues(vals: List[Float], avgx: Float, avgy: Float, avgz: Float): List[Float] = {
    val normalized = new Array[Float](vals.length)
    for (i <- 0 until vals.length / 3) {
      normalized(3 * i) = vals(3 * i) - avgx
      normalized((3 * i) + 1) = vals((3 * i) + 1) - avgy
      normalized((3 * i) + 2) = vals((3 * i) + 2) - avgz
    }
    normalized.toList
  }

  def updateMaxMin(line: String): Unit = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    if (x > maxx) {
      maxx = x
    } else if (x < minx) {
      minx = x
    }
    if (y > maxy) {
      maxy = y
    } else if (y < miny) {
      miny = y
    }
    if (z > maxz) {
      maxz = z
    } else if (z < minz) {
      minz = z
    }
  }

  def extractVertices(line: String): List[Float] = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(x, y, z)
  }


  def extractNormals(line: String): List[Float] = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(x, y, z)

  }

  def extractTexCoords(line: String): List[Float] = {
    //line looks like this:
    //#vt 0.999896 0.983649
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    List(x, y)
  }

  def extractVertexIndecies(line: String): Array[Int] = {
    //line looks like this:
    //#f v1/vt1/vn1 v2/vt2/vn2 v3/vt3/vn3
    //we want (v1, v2, v3)
    //NOTE: indecies in obj files start from 1, so we must subtract 1 to get them starting from zero!
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
    Array(vt1 - 1, vt2 - 1, vt3 - 1)
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