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

case class Mesh(modelPath: String) extends StrictLogging {
  var eboIndicesLength = 0
  var VAO = 0
  var VBO = 0
  var EBO = 0
  val FLOATSIZE = 4

  def loadMesh(): Int = {
    if (!OBJProcessor.makeByteFiles(modelPath)) {
      logger.error("something went wrong with making obj byte files")
    }

    val (bakedVertsString, bakedIndicesPathString) = OBJProcessor.assembleBakedPaths(modelPath)

    val bakedVertsFile = new File(bakedVertsString)
    val bakedVertsPath = Paths.get(bakedVertsString)

    val bakedIndicesFile = new File(bakedIndicesPathString)
    val bakedIndicesPath = Paths.get(bakedIndicesPathString)

    val vSize = bakedVertsFile.length()
    val iSize = bakedIndicesFile.length()

    logger.info("loading mesh..")
    VAO = glGenVertexArrays()
    glBindVertexArray(VAO)

    VBO = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, VBO)

    EBO = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBO)

    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, iSize, GL_STATIC_DRAW)
    val mappedIBuffer = glMapBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_READ_WRITE).asIntBuffer()
    val indexBytesArray = Files.readAllBytes(bakedIndicesPath)
    val tmpBuffer = ByteBuffer.wrap(indexBytesArray)
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
    glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 3 * FLOATSIZE + 2 * FLOATSIZE, 3 * FLOATSIZE)

    val mappedVBuffer = glMapBuffer(GL_ARRAY_BUFFER, GL_READ_WRITE).asFloatBuffer()
    val vertBytesArray = Files.readAllBytes(bakedVertsPath)
    val tmpByteBuffer = ByteBuffer.wrap(vertBytesArray)
    while (tmpByteBuffer.hasRemaining) {
      val floatval = tmpByteBuffer.getFloat
      mappedVBuffer.put(floatval)
    }
    glUnmapBuffer(GL_ARRAY_BUFFER)

    glBindBuffer(GL_ARRAY_BUFFER, 0)
    glBindVertexArray(0)
    logger.info("mesh loaded")
    VAO
  }
}

object Mesh {
  //Add any new model here لو سمحتم
  val Sphere = Mesh("./assets/models/sphere.obj")
  val Cube = Mesh("./assets/models/cube.obj")

}
