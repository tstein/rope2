package net.tedstein.rope

import java.nio.FloatBuffer

import net.tedstein.rope.Shader.{compileShaderProgram, createShaderObject}
import org.lwjgl.glfw.GLFW.{GLFW_CONTEXT_VERSION_MAJOR, GLFW_CONTEXT_VERSION_MINOR, GLFW_KEY_ESCAPE, GLFW_OPENGL_CORE_PROFILE, GLFW_OPENGL_FORWARD_COMPAT, GLFW_OPENGL_PROFILE, GLFW_PRESS, GLFW_RESIZABLE, GLFW_VISIBLE, glfwCreateWindow, glfwGetKey, glfwGetPrimaryMonitor, glfwGetVideoMode, glfwInit, glfwMakeContextCurrent, glfwPollEvents, glfwSetErrorCallback, glfwSetWindowPos, glfwShowWindow, glfwSwapBuffers, glfwTerminate, glfwWindowHint, glfwWindowShouldClose}
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, GLFWvidmode}
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_FALSE, GL_FLOAT, GL_TRUE, glClear, glClearColor}
import org.lwjgl.opengl.GL15.{GL_ARRAY_BUFFER, glBindBuffer}
import org.lwjgl.opengl.GL20.{GL_FRAGMENT_SHADER, GL_VERTEX_SHADER, glUseProgram}
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil
import org.lwjgl.{BufferUtils, Sys}



class Rope {
  var errorCallback: GLFWErrorCallback = null
  var keyCallback: GLFWKeyCallback = null
  var vertexPath = "./src/net/tedstein/rope/vertex.shader"
  var fragmentPath = "./src/net/tedstein/rope/fragment.shader"
  var scaleLocation = 0

  def run(): Unit = {

    System.out.println("Hello LWJGL " + Sys.getVersion + "!")
    System.out.println("OS: " + System.getProperty("os.name"))
    System.out.println("OS Version: " + System.getProperty("os.version"))
    System.out.println("LWJGL Version: " + org.lwjgl.Sys.getVersion)

    try {
      val window = createOpenglWindow()
      System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION))
      renderScene(window)
    } finally {
      glfwTerminate()
      errorCallback.release()

    }
  }

  def createOpenglWindow(): Long = {
    errorCallback = new GLFWErrorCallback {
      override def invoke(i: Int, l: Long): Unit = {}
    }
    glfwSetErrorCallback(errorCallback)
    if (glfwInit() != GL11.GL_TRUE)
      throw new IllegalStateException("Unable to initialize GLFW")

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)

    val WIDTH = 1024
    val HEIGHT = 768
    val window: Long = glfwCreateWindow(WIDTH, HEIGHT, "Rope", MemoryUtil.NULL, MemoryUtil.NULL)

    if (window < 0)
      throw new RuntimeException("Failed to create the GLFW window")

    glfwMakeContextCurrent(window)
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )

    GLContext.createFromCurrent()
    glfwShowWindow(window)
    window

  }

  def renderScene(window: Long): Unit = {
    var scale: Float = 0.0f
    val vs = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    val fs = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    val program = compileShaderProgram(vs, fs)
    glUseProgram(program)

    //create Vertex Array Object that will hold all the VBOs and IBOs
    val vao: Int = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    //create Vertex Buffer Object that will hold information about vertices
    //here it holds the position and color of each vertex
    val vbo: Int = GL15.glGenBuffers()

    val verts: FloatBuffer = BufferUtils.createFloatBuffer(12)
    val v = Array(-1.0f, -1.0f, 0.0f,
    0.0f, -1.0f, 1.0f,
    1.0f, -1.0f, 0.0f,
    0.0f, 1.0f, 0.0f)

    verts.put(v)
    verts.flip()
    glBindBuffer(GL_ARRAY_BUFFER, vbo)
    GL15.glBufferData(GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW)

    //create Index Buffer Object that hold the indecies of the vertices so we can reuse them to draw our rectangle
    val ibo = GL15.glGenBuffers()
    val elems = BufferUtils.createIntBuffer(12)
    val index = Array(0, 3, 1, 1, 3, 2, 2, 3, 0, 0, 1, 2)
    elems.put(index)
    elems.flip()
    glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elems, GL15.GL_STATIC_DRAW)


    val posAttrib = GL20.glGetAttribLocation(program, "position")
    GL20.glEnableVertexAttribArray(posAttrib)
    GL20.glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0) // last two parameters:
                                                                    // stride(distance in bytes between 2 vertex positions),
                                                                    // offset in bytes from start of array

    val gWorldLocation = GL20.glGetUniformLocation(program, "gWorld")

    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT)
      scale += 0.01f

      var world = Matrix4f()

      /*   p.Scale(sinf(Scale * 0.1f), sinf(Scale * 0.1f), sinf(Scale * 0.1f));
    p.WorldPos(sinf(Scale), 0.0f, 0.0f);
    p.Rotate(sinf(Scale) * 90.0f, sinf(Scale) * 90.0f, sinf(Scale) * 90.0f);*/

      val translate = Transformations.translate(Math.sin(scale).toFloat, 0.0f, 0.0f)
      val rotate = Transformations.rotate(0.0f, 0.0f, 0.0f, 0.0f)
      val scaling = Transformations.scale(1.0f, 1.0f, 1.0f)


      world = Transformations.getModelTransformMatrix(translate, rotate, scaling)

      val worldbuffer: FloatBuffer = Matrix4f.getFloatBuffer(world)

      GL20.glUniformMatrix4fv(gWorldLocation, true, worldbuffer)

      GL11.glDrawElements(GL11.GL_TRIANGLES, 12, GL11.GL_UNSIGNED_INT, 0)

      glfwSwapBuffers(window)
      glfwPollEvents()
    }



    GL20.glDeleteProgram(program)
    GL20.glDeleteShader(fs)
    GL20.glDeleteShader(vs)
    GL15.glDeleteBuffers(vbo)
    GL30.glDeleteVertexArrays(vao)
  }
}




object Rope {
  def main(args: Array[String]) = {
    println("Up and running!")
    val rope = new Rope()
    rope.run()
  }

}