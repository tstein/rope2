package net.tedstein.rope.graphics

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL12
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths
import org.lwjgl.opengl.GL11._

object TextureLoader {
  private val BYTES_PER_PIXEL = 4

  def loadTexture(image: BufferedImage): Int = {
    val pixels = new Array[Int](image.getWidth * image.getHeight)
    image.getRGB(0, 0, image.getWidth, image.getHeight, pixels, 0, image.getWidth)
    val buffer = BufferUtils.createByteBuffer(image.getWidth * image.getHeight * BYTES_PER_PIXEL)

    for (y <- 0 until image.getHeight;
         x <- 0 until image.getWidth) {
      val pixel = pixels(y * image.getWidth + x)
      buffer.put(((pixel >> 16) & 0xFF).toByte)
      buffer.put(((pixel >> 8) & 0xFF).toByte)
      buffer.put((pixel & 0xFF).toByte)
      buffer.put(((pixel >> 24) & 0xFF).toByte)
    }
    buffer.flip

    val textureID: Int = glGenTextures
    glBindTexture(GL_TEXTURE_2D, textureID)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.getWidth, image.getHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
    textureID
  }

  def loadImage(loc: String): BufferedImage = {
      ImageIO.read(Files.newInputStream(Paths.get(loc)))
  }
}