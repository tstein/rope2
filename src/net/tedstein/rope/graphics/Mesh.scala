package net.tedstein.rope.graphics

import java.io.{DataInputStream, FileInputStream, File}
import java.nio.file.{Paths, Files}
import java.nio.{ByteOrder, ByteBuffer, FloatBuffer, IntBuffer}

import com.typesafe.scalalogging.StrictLogging
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl._

import scala.io.Source

case class Mesh(modelPath: String) extends StrictLogging {
  var eboIndices = List[Int]()
  var eboIndicesLength = 0
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
     // OBJLoader.parseObjFile(modelPath, this)
    if (!OBJProcessor.makeByteFiles(modelPath)) {
      logger.error("something went wrong with making obj byte files")
    }

    val modelBytesFile = new File("verts.bin")
    println(modelPath)
    val vSize = modelBytesFile.length()
    println("vSize: " + vSize)


    val indicesByteFile = new File("indices.bin")
    val iSize = indicesByteFile.length()
    println("iSize: " + iSize)

    val currPath = Paths.get("")
    val s = currPath.toAbsolutePath.toString
    System.out.println("Current relative path is: " + s)

    println("")
    VAO = glGenVertexArrays()
    glBindVertexArray(VAO)

    VBO = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, VBO)

    EBO = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)

    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, iSize, GL_STATIC_DRAW)
    println("error after buffering data: " + GL11.glGetError())
    val mappedIBuffer = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_READ_WRITE).asIntBuffer()
    println("error after mapping buffer: " + GL11.glGetError())
    val indexFileBytes = Files.readAllBytes(Paths.get("indices.bin"))
    val tmpBuffer = ByteBuffer.wrap(indexFileBytes)
    while (tmpBuffer.hasRemaining) {
      val intval = tmpBuffer.getInt()
      eboIndicesLength = eboIndicesLength + 1
      mappedIBuffer.put(intval)
    }
    glUnmapBuffer(GL_ELEMENT_ARRAY_BUFFER)

    GL15.glBufferData(GL_ARRAY_BUFFER, vSize, GL_STATIC_DRAW)
    glEnableVertexAttribArray(0)
    glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE, 0)
    glEnableVertexAttribArray(1)
    glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE,  3 * FLOATSIZE)

    val mappedVBuffer = glMapBuffer(GL_ARRAY_BUFFER, GL_READ_WRITE).asFloatBuffer()
    val vertFileBytes = Files.readAllBytes(Paths.get("verts.bin"))
    val tmpByteBuffer = ByteBuffer.wrap(vertFileBytes)
    while (tmpByteBuffer.hasRemaining) {
      val floatval = tmpByteBuffer.getFloat
      mappedVBuffer.put(floatval)
    }
    glUnmapBuffer(GL_ARRAY_BUFFER)

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
