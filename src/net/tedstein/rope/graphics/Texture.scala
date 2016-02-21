package net.tedstein.rope.graphics

import java.io.{FileInputStream, InputStream}
import java.nio.ByteBuffer

import de.matthiasmann.twl.utils.PNGDecoder
import org.lwjgl.opengl._

case class Texture(name: String)

object Texture {
  // Adding a new texture? Make sure to add it to AllTextures below.
  val Sun = Texture("sun")
  val Earth = Texture("earth")
  val Moon = Texture("moon")

  val AllTextures = Set(Sun, Earth, Moon)
}