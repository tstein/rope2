package net.tedstein.rope.graphics

import java.nio.{IntBuffer, FloatBuffer}

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


case class Mesh(modelPath: String) {

  var vertices =  List[Float]()
  var vertIndecies = List[Int]()
  var normals = List[Float]()
  var normalIndecies = List[Int]()
  var texCoords = List[Float]()
  var texIndecies = List[Int]()
  var faces =  List[Int]()


  var VAO = 0
  var VBO = 0
  var EBO = 0
  val FLOATSIZE = 4


  def graphicsFlatten(verts: List[Float], texes: List[Float]): FloatBuffer = {
    var output = new ArrayBuffer[Float](verts.length + texes.length)
    for (i <- 0 to (verts.length / 3) - 1) {
      output += verts(3 * i)
      output += verts(3 * i + 1)
      output += verts(3 * i + 2)

      output += texes(2 * i + 0)
      output += texes(2 * i + 1)
    }

    val buff = BufferUtils.createFloatBuffer(output.length)

    val outputArray = output.toArray
    buff.put(outputArray)
    buff.flip()
    buff
  }

  def setupMesh(): Int = {
    OBJLoader.parseObjFile(modelPath, this)
    VAO = glGenVertexArrays()
    glBindVertexArray(VAO)

    VBO = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, VBO)

    EBO = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)

    var vertBuffer = BufferUtils.createFloatBuffer(vertices.length + texCoords.length)
    vertBuffer = graphicsFlatten(vertices, texCoords)
    GL15.glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW)

    val indexBuffer = getVertIndexBuffer()
    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

    glEnableVertexAttribArray(0)
    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, true, 3 * FLOATSIZE + 2 * FLOATSIZE, 0)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 2, GL11.GL_FLOAT, true, 3 * FLOATSIZE + 2 * FLOATSIZE,  3 * FLOATSIZE)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
    VAO
  }

  def getVertIndexBuffer(): IntBuffer = {
    val indexBuffer = BufferUtils.createIntBuffer(vertIndecies.length)
    indexBuffer.put(vertIndecies.toArray)
    indexBuffer.flip()
    indexBuffer
  }

  def getVertBuffer(): FloatBuffer = {
    var vertBuffer = BufferUtils.createFloatBuffer(vertices.length + texCoords.length)
    vertBuffer = graphicsFlatten(vertices, texCoords)
    vertBuffer
  }

  def getVertCount(): Int = {
    vertices.length + texCoords.length
  }

  def getIndeciesCount(): Int = {
    vertIndecies.length
  }
  def hasNormals: Boolean = {
   normals.nonEmpty
  }
}
