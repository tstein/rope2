package net.tedstein.rope.graphics

import java.nio.ByteBuffer

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12
import org.lwjgl.stb.STBImage.stbi_load

case class LoadedImage(bytes: ByteBuffer, width: Int, height: Int)

object TextureLoader {
  private val BytesPerPixel = 4
  private val TextureRoot = "assets"

  def loadTexture(image: LoadedImage): Int = {
    val textureID: Int = glGenTextures
    glBindTexture(GL_TEXTURE_2D, textureID)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image.bytes)
    textureID
  }

  def loadImage(path: String): LoadedImage = {
    val width = BufferUtils.createIntBuffer(1)
    val height = BufferUtils.createIntBuffer(1)
    val numPixels = BufferUtils.createIntBuffer(1)
    val imageBytes = stbi_load(path, width, height, numPixels, BytesPerPixel)
    LoadedImage(imageBytes, width.get(0), height.get(0))
  }

  def loadImages(textureNames: Seq[String]): Map[String, LoadedImage] = {
    textureNames.map(name => name -> loadImage(s"$TextureRoot/$name.jpg")).toMap.seq
  }
}