package net.tedstein.rope


import org.lwjgl.BufferUtils

import scala.io.Source
import java.io.FileInputStream
import org.lwjgl.opengl.GL20.{glCreateShader, glShaderSource, glCompileShader, glGetShaderi,glGetShaderInfoLog, glAttachShader}
import org.lwjgl.opengl.GL20.{GL_COMPILE_STATUS, GL_LINK_STATUS, glCreateProgram, glDeleteShader, glLinkProgram, glGetProgrami, glGetProgramInfoLog}
import org.lwjgl.opengl.GL11.{GL_FALSE, GL_TRUE}


/**
 * Created by ruba on 11/1/15.
 */
object Shader {

  def createShaderObject(shaderType: Int, shaderFile: String): Int = {
    val shader: Int = glCreateShader(shaderType)
    if (shader == 0) {
      //throw exception here or something
    }

    val shaderSrc: String = readFileAsString(shaderFile)

    glShaderSource(shader, shaderSrc)
    glCompileShader(shader)


    //whatever the info log says just print it
    System.out.print(glGetShaderInfoLog(shader, 512))

    val success: Int = glGetShaderi(shader, GL_COMPILE_STATUS)

    //check if shader compilation failed
    if (success == GL_FALSE) {
      System.out.println("Failed in compiling shader")
      sys.exit() //this is maybe not a good idea
    } else if (success == GL_TRUE) {
      System.out.println("Succeeded in compiling shader " + shader)
    }

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
  def compileShaderProgram(vertexShader: Int, fragmentShader: Int): Int = {
    val shaderProgram: Int = glCreateProgram()
    if (shaderProgram == 0) {
      //we 'ave un problem
    }

    glAttachShader(shaderProgram, vertexShader)
    glAttachShader(shaderProgram, fragmentShader)
    glLinkProgram(shaderProgram)

    //print out info log
    System.out.println(glGetProgramInfoLog(shaderProgram, 512))


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

      return shaderProgram
  }


}


