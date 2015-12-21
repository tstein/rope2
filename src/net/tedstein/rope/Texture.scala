package net.tedstein.rope

import java.io.{FileInputStream, InputStream}
import java.nio.ByteBuffer

import de.matthiasmann.twl.utils.PNGDecoder
import org.lwjgl.opengl._

object Texture {

  def loadPNGTexture(imagePath: String, textureUnit: Int): Int = {
    var tWidth: Int = 0
    var tHeight: Int = 0
    var buf: ByteBuffer = null

    try {
      // Open the PNG file as an InputStream
      val in: InputStream = new FileInputStream(imagePath)
      // Link the PNG decoder to this stream
      val decoder: PNGDecoder = new PNGDecoder(in)
      // Get the width and height of the texture
      tWidth = decoder.getWidth
      tHeight = decoder.getHeight
      // Decode the PNG file in a ByteBuffer
      buf = ByteBuffer.allocateDirect(4 * decoder.getWidth * decoder.getHeight)
      decoder.decode(buf, decoder.getWidth * 4, PNGDecoder.Format.RGBA)
      buf.flip()
      in.close()
    } catch {
      case ex: Exception => throw ex
    }

    // Create a   new texture object in memory and bind it
    val texId: Int = GL11.glGenTextures()
    GL13.glActiveTexture(textureUnit)
    GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId)

    // All RGB bytes are aligned to each other and each component is 1 byte
    GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

    // Upload the texture data and generate mip maps (for scaling)
    GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, tWidth, tHeight, 0,
      GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf)
    GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)

    // Setup the ST coordinate system
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL14.GL_MIRRORED_REPEAT)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL14.GL_MIRRORED_REPEAT)

    // Setup what to do when the texture has to be scaled
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
      GL11.GL_NEAREST)
    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
      GL11.GL_LINEAR_MIPMAP_LINEAR)

    GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
    texId
  }
}