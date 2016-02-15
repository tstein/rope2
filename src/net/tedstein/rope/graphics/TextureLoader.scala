package net.tedstein.rope.graphics

import java.nio.ByteBuffer
import java.nio.file.{Files, Paths}

import com.typesafe.scalalogging.StrictLogging
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12
import org.lwjgl.stb.STBImage.stbi_load

case class LoadedImage(bytes: ByteBuffer, width: Int, height: Int)

object TextureLoader extends StrictLogging {
  private val BytesPerPixel = 4
  private val TextureRoot = Paths.get("assets", "textures")
  private val ValidExtensions = Seq("png", "jpg", "jpeg")

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

  private def loadImage(textureName: String): Option[LoadedImage] = {
    val path = ValidExtensions.filter(ext => Files.exists(TextureRoot.resolve(s"$textureName.$ext"))) match {
      case Nil =>
        logger.error(s"Couldn't find a texture with name $textureName and extension in $ValidExtensions!")
        return None
      case ext :: exts =>
        if (exts.nonEmpty) {
          logger.warn(s"Found multiple textures for name $textureName. Ignoring extensions in $exts.")
        }
        TextureRoot.resolve(s"$textureName.$ext").toString
    }

    val width = BufferUtils.createIntBuffer(1)
    val height = BufferUtils.createIntBuffer(1)
    val numPixels = BufferUtils.createIntBuffer(1)
    val imageBytes = stbi_load(path, width, height, numPixels, BytesPerPixel) match {
      case null => return None
      case bytes => bytes
    }
    Some(LoadedImage(imageBytes, width.get(0), height.get(0)))
  }

  def loadImages(textureNames: Seq[String]): Map[String, LoadedImage] = {
    textureNames.par.flatMap(name => loadImage(name).map(name -> _)).toMap.seq
  }
}
