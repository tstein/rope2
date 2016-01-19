package net.tedstein.rope.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._

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
  var indecies = List[Int]()
  var normals = List[Float]()
  var texCoords = List[Float]()
  var faces =  List[Int]()
  var z = Array[Float]()

  var VAO = 0
  var VBO = 0
  var EBO = 0

  def draw(): Unit = {}

  def setupMesh(): Unit = {
    VAO = glGenVertexArrays()
    VBO = glGenBuffers()
    EBO = glGenBuffers()

    val vertBuffer = BufferUtils.createFloatBuffer(vertices.length)
    vertBuffer.put(vertices.toArray)
    vertBuffer.flip()

    val indexBuffer = BufferUtils.createIntBuffer(indecies.length)
    indexBuffer.put(indecies.toArray)
    indexBuffer.flip()

    glBindVertexArray(VAO)
    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    GL15.glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)
    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)
   // val posAttrib = GL20.glGetAttribLocation(program, "position")
    glEnableVertexAttribArray(VAO)



  }
}
