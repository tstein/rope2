package net.tedstein.rope

import java.nio.FloatBuffer
import net.tedstein.rope.Shader.{createShaderObject, compileShaderProgram}

import org.lwjgl.glfw.GLFW.{GLFW_CONTEXT_VERSION_MAJOR, GLFW_CONTEXT_VERSION_MINOR, GLFW_OPENGL_CORE_PROFILE, GLFW_OPENGL_FORWARD_COMPAT, GLFW_OPENGL_PROFILE}
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, GLFWvidmode}
import org.lwjgl.opengl._

import org.lwjgl.glfw.GLFW.{GLFW_KEY_ESCAPE, GLFW_RELEASE, GLFW_RESIZABLE, GLFW_VISIBLE, glfwCreateWindow, glfwGetPrimaryMonitor, glfwGetVideoMode, glfwInit, glfwMakeContextCurrent, glfwPollEvents, glfwSetErrorCallback, glfwSetWindowPos, glfwSetWindowShouldClose, glfwShowWindow, glfwSwapBuffers, glfwTerminate, glfwWindowHint, glfwWindowShouldClose}
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_FALSE, GL_FLOAT, GL_TRIANGLES, GL_TRUE, GL_VERTEX_ARRAY, glClear, glClearColor, glColorPointer, glDisableClientState, glDrawArrays, glEnableClientState, glRotatef, glVertexPointer}
import org.lwjgl.opengl.GL20.{GL_VERTEX_SHADER, GL_FRAGMENT_SHADER, glUseProgram}
import org.lwjgl.system.MemoryUtil
import org.lwjgl.{BufferUtils, Sys}
import org.lwjgl.glfw.GLFW.{glfwTerminate, glfwGetKey, GLFW_PRESS}
import org.lwjgl.opengl.GL15.{glGenBuffers, glBindBuffer, glBufferData, GL_ARRAY_BUFFER, GL_STATIC_DRAW}

class Rope {
  var errorCallback: GLFWErrorCallback = null
  var keyCallback: GLFWKeyCallback = null
  var vertexPath: String = "/Users/ruba/code/rope2/src/net/tedstein/rope/vertex.shader"
  var fragmentPath: String = "/Users/ruba/code/rope2/src/net/tedstein/rope/fragment.shader"
  var scaleLocation: Int = 0



  def run(): Unit = {

    System.out.println("Hello LWJGL " + Sys.getVersion + "!")
    System.out.println("OS: " + System.getProperty("os.name"))
    System.out.println("OS Version: " + System.getProperty("os.version"))
    System.out.println("LWJGL Version: " + org.lwjgl.Sys.getVersion())

    try {
      val window = createOpenglWindow()
      System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION))
      val (vertexArrayID, vboiId) = loadCube(1, 2, 3)
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

    val vs = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    val fs = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    val program = compileShaderProgram(vs, fs)


    var scale: Float = 0.0f
    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT)
      glUseProgram(program)
      scale += 0.001f
      GL20.glUniform1f(scaleLocation, scale)


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



  def loadCube(x: Float, y: Float, z: Float): (Int, Int) = {
    //awful looking code what was I thinking :O
    var vertices: FloatBuffer = BufferUtils.createFloatBuffer(24 * 4)
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

    var colors: FloatBuffer = BufferUtils.createFloatBuffer(24 * 4)
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

    val indices = Array[Byte](
      0, 1, 2, 2, 3, 0,
      3, 2, 6, 6, 7, 3,
      7, 6, 5, 5, 4, 7,
      4, 0, 3, 3, 7, 4,
      0, 1, 5, 5, 4, 0,
      1, 5, 6, 6, 2, 1
    )

    val indicesBuffer = BufferUtils.createByteBuffer(24*4)
    indicesBuffer.put(indices)
    indicesBuffer.flip()


    /* We have a color array and a vertex array */
    //create VAO and bind it
    val vertexArrayID: Int = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vertexArrayID)
    //create VBO and bind it
    val vertexBufferID = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBufferID)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)


    // Create a new VBO for the indices and select it (bind) - COLORS
    val vboColorId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboColorId)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colors, GL15.GL_STATIC_DRAW)
    GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 0)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)

    // Deselect (bind to 0) the VAO
    GL30.glBindVertexArray(0)
    // Create a new VBO for the indices and select it (bind) - INDICES
    val vboIndexId = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndexId)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0)

    return (vertexArrayID, vboIndexId)
  }

  def loadTriangle(): Int ={
    var verts: FloatBuffer = BufferUtils.createFloatBuffer(9)
    val v1: Array[Float] = Array(-1.0f, -1.0f, 0.0f)
    val v2: Array[Float] = Array(1.0f, -1.0f, 0.0f)
    val v3: Array[Float] = Array(0.0f, 1.0f, 0.0f)
    verts.put(v1).put(v2).put(v3)
    val VBO: Int = glGenBuffers()
    glBindBuffer(GL_ARRAY_BUFFER, VBO)
    glBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW)
    return VBO

  }

}


object Rope {
  def main(args: Array[String]) = {
    println("Up and running!")
    val rope = new Rope()
    rope.run()
  }

}