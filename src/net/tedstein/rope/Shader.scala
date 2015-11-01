package net.tedstein.rope

import scala.io.Source
import java.io.FileInputStream
import org.lwjgl.opengl.GL20.{glCreateShader, glShaderSource, glCompileShader, glGetShaderiv, glCreateProgram}
/**
 * Created by ruba on 11/1/15.
 */
object Shader {
  def createShader(shaderType: Int, shaderFile: String): Int = {
    val shader: Int = glCreateShader(shaderType)
    if (shader == 0) {
      //throw exception here or something
    }

    val shaderSrc: String = readFileAsString(shaderFile)
    glShaderSource(shader, shaderSrc)
    glCompileShader(shader)

    return shader
  }

  def readFileAsString(filename: String): String = {
    //TODO: add error checking!
    val source: StringBuilder = new StringBuilder()
    val in: FileInputStream = new FileInputStream(filename)

    try {
      for (line <- Source.fromFile(filename).getLines()) {
        source.append(line).append('\n')
      }
    } catch {
      case ex: Exception => throw ex
    }

    return source.toString()
  }

  //one program for all shaders so this needs fixing
  def addShaderToProgram(shader: Int): Int = {
    var shaderProgram: Int = glCreateProgram()
    if (shaderProgram == 0) {
      //we 'ave un problem
    }

    return 0

  }
}


