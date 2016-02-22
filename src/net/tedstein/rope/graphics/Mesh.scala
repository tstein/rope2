package net.tedstein.rope.graphics

import java.nio.{FloatBuffer, IntBuffer}

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.{GL11, GL15}

case class Mesh(modelPath: String) {
  var eboIndices = Array[Int]()
  var packedverts = Array[Float]()

  var vertices =  List[Float]()
  var vertIndices = List[Int]()
  var normals = List[Float]()
  var normalIndices = List[Int]()
  var texCoords = List[Float]()
  var texIndices = List[Int]()


  var VAO = 0
  var VBO = 0
  var EBO = 0
  val FLOATSIZE = 4


  def setupMesh(): Int = {
    OBJLoader.parseObjFile(modelPath, this)
    VAO = glGenVertexArrays()
    glBindVertexArray(VAO)

    VBO = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, VBO)

    EBO = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)

    val indexBuffer = makeIntBuffer(eboIndices.toArray)
    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW)

    var vertBuffer = BufferUtils.createFloatBuffer(vertices.length)
    vertBuffer = makeFloatBuffer(packedverts)
    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    GL15.glBufferData(GL_ARRAY_BUFFER, vertBuffer, GL15.GL_STATIC_DRAW)

    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE, 0)

    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE,  3 * FLOATSIZE)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
    VAO
  }

  def makeIntBuffer(in: Array[Int]): IntBuffer = {
    val buff = BufferUtils.createIntBuffer(in.length)
    buff.put(in)
    buff.flip()
    buff
  }

  def makeFloatBuffer(in: Array[Float]): FloatBuffer = {
    val buff = BufferUtils.createFloatBuffer(in.length)
    buff.put(in)
    buff.flip()
    buff
  }

  def hasNormals: Boolean = {
   normals.nonEmpty
  }
}
