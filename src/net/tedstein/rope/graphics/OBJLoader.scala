package net.tedstein.rope.graphics

import com.typesafe.scalalogging.StrictLogging

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
        model.newVerts ++= extractNewVertices(line)
        updateMaxMin(line)
        unorderedVertices ++= extractVertices(line)
      } else if (prefix == "vt") {
        unorderedTexes ++= extractTexCoords(line)
        model.newTexes ++= extractNewTexes(line)
      } else if (prefix == "f") {
        model.vertIndecies ++= extractVertexIndecies(line)
        model.texIndecies ++= extractTexCoordIndecies(line)
      }
    }

    val avgx = (maxx + minx) / 2
    val avgy = (maxy  + miny) / 2
    val avgz = (maxz + minz) / 2

    model.vertices ++= normalizeValues(unorderedVertices, avgx, avgy, avgz)
    model.texCoords ++= reorderTexes(unorderedTexes, model.vertIndecies)

    ////NOTE: I'm trying to do the interleaving with this line directly but it gives me the same result
    //var packed = packVertexData(model.vertices, model.texCoords, model.vertIndecies, model.texIndecies)
    // model.packedverts ++= packed
  }

  def normalizeTextures(texes: List[Float]): List[Float] = {
    println("texes: " + texes)
    val normalized = new Array[Float](texes.length)
    for (i <- 0 to (texes.length / 2) - 1) {
      normalized(2 * i) = texes(2 * i)
      normalized((2 * i) + 1) = 1 - texes((2 * i) + 1)
    }
    println(normalized.toList)
    normalized.toList
  }
  def normalizeValues(vals: List[Float], avgx: Float, avgy: Float, avgz: Float): List[Float] = {
    val normalized = new Array[Float](vals.length)
    for (i <- 0 to (vals.length / 3) - 1) {
      normalized(3 * i) = vals(3 * i) - avgx
      normalized((3 * i) +  1) = vals((3 * i) + 1) - avgy
      normalized((3 * i) + 2) = vals((3 * i) + 2) - avgz
    }
    normalized.toList
  }


  def packVertexData(verts: List[Float], texes: List[Float], vertIndecies: List[Int], texIndecies: List[Int]): Array[Float] = {
    //(1, 2, 3, 4, 5, 6, 7, 8, 9)
    //(a, b, c, d, e, f)
    //(1, 2, 3, a, b, 4, 5, 6, c, d, 7, 8, 9, e, f)
    //i = 0 || (1,
    //i = 1 ||
    //i = 2 ||
    val packed = new Array[Float](verts.length + texes.length)
    println("len: " + (verts.length + texes.length))
    for (i <- 0 to (verts.length / 3) - 1) {
      /*
      println("i: " + i)
      println("correct vert index: " + (vertIndecies(i)))
      println("correct tex index: " + (texIndecies(i)))
      println("vert(" + i + ")(0): " + verts((i)))
      println("vert(" + i + ")(1): " + verts((i)))
      println("vert(" + i + ")(2): " + verts((i)))
      println("tex(" + i + ")(0): " + texes(texIndecies(i)))
      println("tex(" + i + ")(1): " + texes(texIndecies(i)))
*/
      packed.update(5 * i, verts((3 * i))) //0 5 10
      packed.update((5 * i) + 1, verts((3 * i) + 1)) //1 6
      packed.update((5 * i) + 2, verts((3 * i) + 2)) //2 7

      packed.update((5 * i) + 3, texes(texIndecies(2 * i))) //3 8
      packed.update((5 * i) + 4, texes(texIndecies((2 * i)) + 1)) //4 9
      //0 - 14
      //(100, 200, 300, 400, 500, 600, 700, 800, 900)
      //(a, b, c, d, e, f)
      //(100, 200, 300, a, b, 400, 500, 600, c, d, 700, 800, 900, e , f)
      //  0,   1,   2,  3, 4,  5,  6,    7,  8, 9, 10,  11,  12,  13, 14

      //0 1 2 - 3, 4
      // 5 6 7 - 8, 9
      // 10 11 12 - 13, 14
      //3, 4, 8, 9  13 14
    }
    packed
  }

  def reorderTexes(unorderedTexes: List[Float], indecies: List[Int]): List[Float] = {
    val orderedTexes = new Array[Float](indecies.length * 2)
    //(3, 4, 1, 2, 5, 6)
    //(2, 1, 3)
    //i = 0 || 0, 1, 2
    //i = 1 || (2,
    for (i <- 0 to (unorderedTexes.length / 2) - 1) {
      val correctIndex = indecies(i) - 1
      orderedTexes.update(2 * i, unorderedTexes(correctIndex))
      orderedTexes.update(2 * i + 1, unorderedTexes((correctIndex) + 1))
    }
    orderedTexes.toList
  }
  def reorderVertices(unorderedVertices: List[Float], indecies: List[Int]): List[Float] = {
    val orderedVertices = new Array[Float](indecies.length * 3)
    //(e, f, a, b, c, d)
    //(3, 1, 2)
    for (i <- 0 to unorderedVertices.length) {
      println("i: " + i)
      val correctIndex = indecies(i) - 1
      println("correct: " + correctIndex)
      orderedVertices.update(3 * i, unorderedVertices(3 * correctIndex))
      orderedVertices.update(3 * i + 1, unorderedVertices(3 * correctIndex + 1))
      orderedVertices.update(3 * i + 2, unorderedVertices(3 * correctIndex + 2))
    }
    orderedVertices.toList
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

  def extractVertices(line: String): List[Float]= {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(x, y, z)
  }

  def extractNewVertices(line: String): List[List[Float]] = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    val z = line.split(" ")(3).toFloat
    List(List(x, y, z))
  }

  def extractNewTexes(line: String): List[List[Float]] = {
    val x = line.split(" ")(1).toFloat
    val y = line.split(" ")(2).toFloat
    List(List(x, y))
  }

  def graphicsFlatten(verts: List[Float], texes: List[Float]): List[Float] = {

    var output = ArrayBuffer[Float]()
    for (i <- 0 to (verts.length / 3) - 1) {
      output += verts(3 * i ) //0 //3
      output += verts(3 * i + 1) //1 //4
      output += verts(3 * i + 2) //2 //

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

  /*
  def normalizeVertexPositions(verts: List[Float]): List[Float] = {

  }*/
}
