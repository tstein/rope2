package net.tedstein.rope.graphics

import java.nio.{FloatBuffer, ByteBuffer}
import java.nio.file.{Paths, Files}

import com.typesafe.scalalogging.StrictLogging
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl._
import org.lwjgl.stb.STBImage._
import org.lwjgl.stb.STBTTAlignedQuad.Buffer
import org.lwjgl.stb.{STBTTBakedChar, STBTTAlignedQuad}
import org.lwjgl.stb.STBTruetype._
object TextRenderer extends StrictLogging {
  val VERTEXSIZE = 2
  val TEXSIZE = 2
  val FLOATSIZE = 4
  val NUMCHARS = 96// ASCII 32..126 is 95 glyphs
  val BITMAPWIDTH = 512
  val BITMAPHEIGHT = 512
  val charData = STBTTBakedChar.callocBuffer(NUMCHARS)

  def InitFont(font: String): Int = {
    val fontPath = Paths.get(font)
    val fontFile = Files.readAllBytes(fontPath)
    val ttf = BufferUtils.createByteBuffer(fontFile.size)
    ttf.put(fontFile)


    val tmpBitmap = BufferUtils.createByteBuffer(512 * 512)
    //val charData = STBTTBakedChar.mallocBuffer(NUMCHARS)
    stbtt_BakeFontBitmap(ttf, 0, 32.0f, tmpBitmap, BITMAPWIDTH, BITMAPHEIGHT, 32, NUMCHARS, charData)
    val loaded = LoadedImage(tmpBitmap, 512, 512)

    println(loaded.height + " " + loaded.width)
    val textureID: Int = glGenTextures
    glBindTexture(GL_TEXTURE_2D, textureID)
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, 512, 512, 0, GL_ALPHA, GL_UNSIGNED_BYTE, tmpBitmap)

    textureID
  }

  def printText(text: String, x: Float, y: Float, size: Float, texID: Int): Unit = {
    val xFB = BufferUtils.createFloatBuffer(2)
    val yFB = BufferUtils.createFloatBuffer(2)

    xFB.put(0, x)
    yFB.put(0, y)
    printTextSTBB(charData, text, xFB, yFB, size, texID)
  }


  def printTextSTBB(charData: STBTTBakedChar.Buffer, text: String, x: FloatBuffer, y: FloatBuffer, size: Float, texID: Int): Unit = {
    for (c <- text) {
      if (c.toInt >= 32 && c.toInt < 128) {
        println("HELLO " +  c)
        val q = STBTTAlignedQuad.malloc()

        stbtt_GetBakedQuad(charData, 512, 512, c.toInt - 32, x, y, q, 1)
        val verts: FloatBuffer = BufferUtils.createFloatBuffer(FLOATSIZE * 4 * 4)
        val v = Array(
          q.x0 * size, q.y0 * size, q.s0, q.t1,
          q.x1 * size, q.y0 * size, q.s1, q.y1,
          q.x1 * size, q.y1 * size, q.s1, q.t0,
          q.x0 * size, q.y1 * size, q.s0, q.t0
        )

        verts.put(v)
        verts.flip()

        val VAO = glGenVertexArrays()
        val VBO = glGenBuffers()
        glBindVertexArray(VAO)
        glBindBuffer(GL_ARRAY_BUFFER, VBO)

        GL13.glActiveTexture(GL13.GL_TEXTURE0)

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID)


        GL15.glBufferData(GL_ARRAY_BUFFER, verts, GL_DYNAMIC_DRAW)

        glEnableVertexAttribArray(0)
        //glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 4 * FLOATSIZE, 0)

        glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0)

        glDrawArrays(GL_QUADS, 0, 4)
        GL15.glBindBuffer(GL_ARRAY_BUFFER, 0)
        GL30.glBindVertexArray(0)

      }



     }

  }

}
