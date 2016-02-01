package net.tedstein.rope.graphics

import java.nio.FloatBuffer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.{GL11, GL15}

import scala.collection.mutable.ArrayBuffer

/**
  * Created by ruba on 1/17/16.
  */
object vertex {
  val position = Vector3f
  val normal = Vector3f
  val textCoord = Vector3f

}


case class Mesh() {
  var vertices = List[Float]()
  var vertIndecies = List[Int]()
  var normals = List[Float]()
  var normalIndecies = List[Int]()
  var texCoords = List[Float]()
  var texIndecies = List[Int]()
  var faces =  List[Int]()
  val indexBuffer = BufferUtils.createIntBuffer(vertIndecies.length)
  var VAO = 0
  var VBO = 0
  var EBO = 0
  val FLOATSIZE = 4


  def graphicsFlatten(verts: List[Float], texes: List[Float]): FloatBuffer = {
    var output = ArrayBuffer[Float]()
    for (i <- 0 to (verts.length / 3) - 1) {
      output += verts(3 * i)
      output += verts(3 * i + 1)
      output += verts(3 * i + 2)

      output += texes(2 * i + 0)
      output += texes(2 * i + 1)
    }
    val buff = BufferUtils.createFloatBuffer(output.length)
    buff.put(output.toArray)
    buff.flip()
    buff
  }

  def setupMesh(): Int = {
    VAO = glGenVertexArrays()
    VBO = glGenBuffers()
    EBO = glGenBuffers()

    var vertBuffer = BufferUtils.createFloatBuffer(vertices.length + texCoords.length)
    vertBuffer = graphicsFlatten(vertices, texCoords)

    indexBuffer.put(vertIndecies.toArray)
    indexBuffer.flip()
    glBindVertexArray(VAO)

    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    GL15.glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW)

    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)
    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE, 0)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE,  3 * FLOATSIZE)
    //(1, 2, 3, a, b, 4, 5, 6, c, d, 7, 8, 9, e, f)
    //byte 0 - 11, 12
    //glEnableVertexAttribArray(VAO)

   // glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
    VAO
  }


  def hasNormals(): Boolean = {
   return normals.length > 0
  }
}
