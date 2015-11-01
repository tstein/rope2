package net.tedstein.rope

import java.nio.FloatBuffer
import net.tedstein.rope.Shader.createShader

import org.lwjgl.glfw.GLFW.{GLFW_CONTEXT_VERSION_MAJOR, GLFW_CONTEXT_VERSION_MINOR, GLFW_OPENGL_CORE_PROFILE, GLFW_OPENGL_FORWARD_COMPAT, GLFW_OPENGL_PROFILE}
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, GLFWvidmode}
import org.lwjgl.opengl._

import org.lwjgl.glfw.GLFW.{GLFW_KEY_ESCAPE, GLFW_RELEASE, GLFW_RESIZABLE, GLFW_VISIBLE, glfwCreateWindow, glfwGetPrimaryMonitor, glfwGetVideoMode, glfwInit, glfwMakeContextCurrent, glfwPollEvents, glfwSetErrorCallback, glfwSetWindowPos, glfwSetWindowShouldClose, glfwShowWindow, glfwSwapBuffers, glfwTerminate, glfwWindowHint, glfwWindowShouldClose}
import org.lwjgl.opengl.GL11.{GL_COLOR_ARRAY, GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_FALSE, GL_FLOAT, GL_TRIANGLES, GL_TRUE, GL_VERTEX_ARRAY, glClear, glClearColor, glColorPointer, glDisableClientState, glDrawArrays, glEnableClientState, glRotatef, glVertexPointer}
import org.lwjgl.opengl.GL20.{GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, glUseProgram}
import org.lwjgl.system.MemoryUtil
import org.lwjgl.{BufferUtils, Sys}
import org.lwjgl.glfw.GLFW.glfwTerminate

class Rope {
  var errorCallback: GLFWErrorCallback = null
  var keyCallback: GLFWKeyCallback = null
  var vertexShader: String = "/Users/ruba/code/rope/src/net/tedstein/rope/SimpleVertexShader.vertexshader"
  var fragmentShader: String = "/Users/ruba/code/rope/src/net/tedstein/rope/SimpleFragmentShader.fragmentshader"
  var scaleLocation: Int = 0



  def run(): Unit = {

    System.out.println("Hello LWJGL " + Sys.getVersion + "!")
    System.out.println("OS: " + System.getProperty("os.name"))
    System.out.println("OS Version: " + System.getProperty("os.version"))
    System.out.println("LWJGL Version: " + org.lwjgl.Sys.getVersion())

    try {
      val window = createOpenglWindow()
      System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION))
     val vertexArrayID = 0
      val vboiId = 0
     // val (vertexArrayID, vboiId) = loadCube(1, 2, 3)
      renderScene(window, vertexArrayID, vboiId)

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

    keyCallback = new GLFWKeyCallback {
      override def invoke(window: Long, pressed_key: Int, scancode: Int, action: Int, mods: Int): Unit = {
        if (pressed_key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, GL_TRUE)
      }
    }
    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )

    GLContext.createFromCurrent()
    glfwShowWindow(window)
    return window

  }

  def renderScene(window: Long, vertexArrayID: Int, vboiId: Int) {

    glClearColor(0.0f, 0.0f, 0.4f, 0.0f)
   val programID = createShader(GL_VERTEX_SHADER, vertexShader)

    var scale: Float = 0.0f
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT)
      glUseProgram(programID)
      scale += 0.001f
      GL20.glUniform1f(scaleLocation, scale)

      /*
      not sure where to put this assertion - the tutorial stuck it in the compileShader function
      scaleLocation = GL20.glGetUniformLocation(programID, "gScale")
      assert(scaleLocation != 0xFFFFFFFF)
      */


      GL30.glBindVertexArray(vertexArrayID)
      GL20.glEnableVertexAttribArray(0)
      GL20.glEnableVertexAttribArray(1)
      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId)
      GL11.glDrawElements(GL11.GL_TRIANGLES, 36, GL11.GL_UNSIGNED_BYTE, 0)

     // val dudes = Universe.region(Dimensions.Position(0, 0, 0), 10, 1)

      GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)
      GL20.glDisableVertexAttribArray(0)
      GL20.glDisableVertexAttribArray(1)
      GL30.glBindVertexArray(0)
      GL20.glUseProgram(0)
      //dudes.foreach(d => drawDumbCube(d.pos.x.toInt, d.pos.y.toInt, d.pos.z.toInt))

      glfwSwapBuffers(window)
      glfwPollEvents()
    }

  }
  /*
    def Run(): Long = {
      glfwSetErrorCallback(errorCallback)
      if (glfwInit() != GL11.GL_TRUE)
        throw new IllegalStateException("Unable to initialize GLFW")

      glfwDefaultWindowHints()
      glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
      glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)

      val WIDTH = 300
      val HEIGHT = 300

      val window: Long = glfwCreateWindow(WIDTH, HEIGHT, "HELLO WORLD", MemoryUtil.NULL, MemoryUtil.NULL)

      if (window == null)
        throw new RuntimeException("Failed to create the GLFW window")

      glfwMakeContextCurrent(window)

      keyCallback = new GLFWKeyCallback {
        override def invoke(window: Long, pressed_key: Int, scancode: Int, action: Int, mods: Int): Unit = {
          if (pressed_key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
            glfwSetWindowShouldClose(window, GL_TRUE)
        }
      }

      val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
      glfwSetWindowPos(
        window,
        (GLFWvidmode.width(vidmode) - WIDTH) / 2,
        (GLFWvidmode.height(vidmode) - HEIGHT) / 2
      )


      glfwSwapInterval(1)
      glfwShowWindow(window)

      return window
    }

    */

  def loop(window: Long) {
    GLContext.createFromCurrent()
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

    //val width: IntBuffer = BufferUtils.createIntBuffer(4)
    //val height: IntBuffer = BufferUtils.createIntBuffer(4)

    while (glfwWindowShouldClose(window) == GL_FALSE) {
     // glfwGetFramebufferSize(window, width, height)
      //glViewport(0, 0, width.get(0), height.get(0))

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)


      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }


  def loadCube(x: Float, y: Float, z: Float): Unit = {
    var vertices: FloatBuffer = BufferUtils.createFloatBuffer(24)
    vertices.put(-1.0f).put(-1.0f).put(-1.0f)
    vertices.put(-1.0f).put(-1.0f).put(1.0f)
    vertices.put(-1.0f).put(1.0f).put(1.0f)
    vertices.put(-1.0f).put(1.0f).put(-1.0f)

    vertices.put(1.0f).put(-1.0f).put(-1.0f)
    vertices.put(1.0f).put(-1.0f).put(1.0f)
    vertices.put(1.0f).put(1.0f).put(1.0f)
    vertices.put(1.0f).put(1.0f).put(-1.0f)

    vertices.put(-1.0f).put(-1.0f).put(-1.0f)
    vertices.put(-1.0f).put(-1.0f).put(1.0f)
    vertices.put(1.0f).put(-1.0f).put(1.0f)
    vertices.put(1.0f).put(-1.0f).put(-1.0f)


    vertices.put(-1.0f).put(1.0f).put(-1.0f)
    vertices.put(-1.0f).put(1.0f).put(1.0f)
    vertices.put(1.0f).put(1.0f).put(1.0f)
    vertices.put(1.0f).put(1.0f).put(-1.0f)


    vertices.put(-1.0f).put(-1.0f).put(-1.0f)
    vertices.put(-1.0f).put(1.0f).put(-1.0f)
    vertices.put(1.0f).put(1.0f).put(-1.0f)
    vertices.put(1.0f).put(-1.0f).put(-1.0f)


    vertices.put(-1.0f).put(-1.0f).put(1.0f)
    vertices.put(-1.0f).put(1.0f).put(1.0f)
    vertices.put(1.0f).put(1.0f).put(1.0f)
    vertices.put(1.0f).put(-1.0f).put(1.0f)

    vertices.flip()

    var colors: FloatBuffer = BufferUtils.createFloatBuffer(24)
    colors.put(0.0f).put(0.0f).put(0.0f)
    colors.put(0.0f).put(0.0f).put(1.0f)
    colors.put(0.0f).put(1.0f).put(1.0f)
    colors.put(0.0f).put(1.0f).put(0.0f)


    colors.put(1.0f).put(0.0f).put(0.0f)
    colors.put(1.0f).put(0.0f).put(1.0f)
    colors.put(1.0f).put(1.0f).put(1.0f)
    colors.put(1.0f).put(1.0f).put(0.0f)


    colors.put(0.0f).put(0.0f).put(0.0f)
    colors.put(0.0f).put(0.0f).put(1.0f)
    colors.put(1.0f).put(0.0f).put(1.0f)
    colors.put(1.0f).put(0.0f).put(0.0f)


    colors.put(0.0f).put(1.0f).put(0.0f)
    colors.put(0.0f).put(1.0f).put(1.0f)
    colors.put(1.0f).put(1.0f).put(1.0f)
    colors.put(1.0f).put(1.0f).put(0.0f)


    colors.put(0.0f).put(0.0f).put(0.0f)
    colors.put(0.0f).put(1.0f).put(0.0f)
    colors.put(1.0f).put(1.0f).put(0.0f)
    colors.put(1.0f).put(0.0f).put(0.0f)


    colors.put(0.0f).put(0.0f).put(1.0f)
    colors.put(0.0f).put(1.0f).put(1.0f)
    colors.put(1.0f).put(1.0f).put(1.0f)
    colors.put(1.0f).put(0.0f).put(1.0f)
    colors.flip()

    var alpha = 0
    //attempt to rotate cube
    glRotatef(alpha, 0, 1, 0)

    /* We have a color array and a vertex array */
    glEnableClientState(GL_VERTEX_ARRAY)
    glEnableClientState(GL_COLOR_ARRAY)

    glColorPointer(3, GL_FLOAT, colors)
    glVertexPointer(3, GL_FLOAT, vertices)
    glDrawArrays(GL_TRIANGLES, 0, 24)

    glDisableClientState(GL_COLOR_ARRAY)
    glDisableClientState(GL_VERTEX_ARRAY)
    alpha += 1

  }

}


object Rope {
  def main(args: Array[String]) = {
    println("Up and running!")
    val rope = new Rope()
    rope.run()
  }

}