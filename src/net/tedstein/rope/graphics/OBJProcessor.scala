package net.tedstein.rope.graphics
import java.io._
import java.nio.ByteBuffer
import java.nio.file.{Paths, Files}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

object OBJProcessor {
  var maxx = 0.0f
  var maxy = 0.0f
  var maxz = 0.0f
  var minx = 0.0f
  var miny = 0.0f
  var minz = 0.0f

  def makeByteFiles(path: String): Boolean = {
  //  val file = new File(path)

    if (Files.exists(Paths.get("verts.bin")) && Files.exists(Paths.get("indices.bin"))) {
      //print something that says file exists already
      return true
    }

    var unorderedVertices = List[Float]()
    var unorderedTexes = List[Float]()
    var vertIndices = List[Int]()
    var texIndices = List[Int]()

    for (line <- Source.fromFile(path).getLines()) {
      val prefix = line.split(" ")(0)
      if (prefix == "v") {
        updateMaxMin(line)
        unorderedVertices ++= extractVertices(line)
      } else if (prefix == "vt") {
        unorderedTexes ++= extractTexCoords(line)
      } else if (prefix == "f") {
        vertIndices ++= extractVertexIndecies(line)
        texIndices ++= extractTexCoordIndecies(line)
      }
    }

    val avgx = (maxx + minx) / 2
    val avgy = (maxy + miny) / 2
    val avgz = (maxz + minz) / 2
    var vertices = List[Float]()
    vertices ++= normalizeValues(unorderedVertices, avgx, avgy, avgz)

    val (packedVertsArray, indices) = pack(vertices, unorderedTexes, vertIndices, texIndices)
    val packedVertsFile = "verts.bin"
    val indicesFile = "indices.bin"
    println("packedVertsFile: " + packedVertsFile + "\n")
    println("indicesFile: " + indicesFile + "\n")
    val vFos = new FileOutputStream(packedVertsFile)
    val iFos = new FileOutputStream(indicesFile)
    vFos.write(packedVertsArray)
    iFos.write(indices)
    vFos.close()
    iFos.close()
/*
    val bosVert = new BufferedOutputStream(new FileOutputStream(packedVertsFile))
    Stream.continually(bosVert.write(packedVertsArray))
    bosVert.close()

    val bosIndices = new BufferedOutputStream(new FileOutputStream(indicesFile))
    Stream.continually(bosIndices.write(indices))
    bosIndices.close()
*/
    Files.exists(Paths.get("verts.bin")) && Files.exists(Paths.get("indices.bin"))

  }

  def pack(verts: List[Float], texes: List[Float], vIndices: List[Int], tIndices: List[Int]): (Array[Byte], Array[Byte]) = {
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

    val packedArray = packed.toArray
    val flattened = packedArray.flatten
    val vertsByteBuffer = ByteBuffer.allocate(flattened.length * 4)
    for (i <- flattened.indices) {
      vertsByteBuffer.putFloat(flattened(i))
    }
    val vertsByteArray = vertsByteBuffer.array()

    val indicesArray = newIndices.toArray
    val indicesByteBuffer = ByteBuffer.allocate(indicesArray.length * 4)
    for (i <- indicesArray.indices) {
      indicesByteBuffer.putInt(indicesArray(i))
    }
    val indicesByteArray = indicesByteBuffer.array()


    /*
    val bbuffer = ByteBuffer.allocate(packedArray.length)
    for (i <- packedArray.indices){
      for (j <- packedArray(i).indices) {
        bbuffer.putFloat(packedArray(i)(j))
      }
    }*/
    (vertsByteArray, indicesByteArray)
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
  def extractTexCoords(line: String): List[Float] = {
      //line looks like this:
      //#vt 0.999896 0.983649
      // Blender appears to emit objs with incorrect texture coordinates. u = 1 - u corrects for this.
      val u = 1f - line.split(" ")(1).toFloat
      val v = line.split(" ")(2).toFloat
      List(u, v)
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

}
