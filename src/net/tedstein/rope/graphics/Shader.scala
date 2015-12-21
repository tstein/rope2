package net.tedstein.rope.graphics

import org.lwjgl.opengl.GL11.{GL_FALSE, GL_TRUE}
import org.lwjgl.opengl.GL20.{GL_COMPILE_STATUS, GL_LINK_STATUS, glAttachShader, glCompileShader, glCreateProgram, glCreateShader, glDeleteShader, glGetProgramInfoLog, glGetProgrami, glGetShaderInfoLog, glGetShaderi, glLinkProgram, glShaderSource}

import scala.io.Source

object Shader {

  def createShaderObject(shaderType: Int, shaderFile: String): Int = {
    val shader: Int = glCreateShader(shaderType)
    if (shader == 0) {
      //throw exception here or something
    }

    val shaderSrc: String = readFileAsString(shaderFile)
    glShaderSource(shader, shaderSrc)
    glCompileShader(shader)

    val success: Int = glGetShaderi(shader, GL_COMPILE_STATUS)
    //check if shader compilation failed
    if (success == GL_FALSE) {
      System.out.println("Failed in compiling shader" + shaderFile)
      sys.exit() //this is maybe not a good idea
    } else if (success == GL_TRUE) {
      System.out.println("Succeeded in compiling shader " + shaderFile)
    }

    shader
  }

  def readFileAsString(filename: String): String = {
    //TODO: add error checking!
    val source: StringBuilder = new StringBuilder()
    //val in: FileInputStream = new FileInputStream(filename)

    try {
      for (line <- Source.fromFile(filename).getLines()) {
        source.append(line).append('\n')
      }
    } catch {
      case ex: Exception => throw ex
    }

    source.toString()
  }

  //one program for all shaders so this needs fixing
  def compileShaderProgram(vertexShader: Int, fragmentShader: Int): Int = {
    val shaderProgram: Int = glCreateProgram()
    if (shaderProgram == 0) {
      //we 'ave un problem
    }

    glAttachShader(shaderProgram, vertexShader)
    glAttachShader(shaderProgram, fragmentShader)
    glLinkProgram(shaderProgram)

    //keeps returning empty strings.. this may be a bug
    System.out.println("Shader Info Log length: " + glGetShaderInfoLog(vertexShader, 512).length())
    //print out info log
    System.out.println("Shader Info Log:" + glGetProgramInfoLog(shaderProgram, 512))


    //check if linking failed
    val success = glGetProgrami(shaderProgram, GL_LINK_STATUS)

    if (success == GL_FALSE) {
      System.out.println("Error linking program")
      return 0
    } else if (success == GL_TRUE) {
      System.out.println("Succeeded in linking program!")

    }
      glDeleteShader(vertexShader)
      glDeleteShader(fragmentShader)

      shaderProgram
  }


}


