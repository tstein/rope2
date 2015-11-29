package net.tedstein.rope

import java.nio.FloatBuffer

import net.tedstein.rope.Shader.{compileShaderProgram, createShaderObject}
import org.lwjgl.glfw.GLFW.{GLFW_CONTEXT_VERSION_MAJOR, GLFW_CONTEXT_VERSION_MINOR, GLFW_KEY_ESCAPE, GLFW_OPENGL_CORE_PROFILE, GLFW_OPENGL_FORWARD_COMPAT, GLFW_OPENGL_PROFILE, GLFW_PRESS, GLFW_RESIZABLE, GLFW_VISIBLE, glfwCreateWindow, glfwGetKey, glfwGetPrimaryMonitor, glfwGetVideoMode, glfwInit, glfwMakeContextCurrent, glfwPollEvents, glfwSetErrorCallback, glfwSetWindowPos, glfwShowWindow, glfwSwapBuffers, glfwTerminate, glfwWindowHint, glfwWindowShouldClose}
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, GLFWvidmode}
import org.lwjgl.opengl.GL11.{GL_COLOR_BUFFER_BIT, GL_FALSE, GL_FLOAT, GL_TRUE, glClear, glClearColor}
import org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER
import org.lwjgl.opengl.GL20.{GL_FRAGMENT_SHADER, GL_VERTEX_SHADER, glUseProgram}
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil
import org.lwjgl.{BufferUtils, Sys}

class Rope {
  var errorCallback: GLFWErrorCallback = null
  var keyCallback: GLFWKeyCallback = null
  var vertexPath = "./src/net/tedstein/rope/vertex.shader"
  var fragmentPath = "./src/net/tedstein/rope/fragment.shader"
  var imagePath = "/Users/ruba/code/rope2/lib/300px-Companion_Cube.png"

  val WIDTH = 1024
  val HEIGHT = 768
  var gVAO: Int = 0
  var gVBO: Int = 0
  var program: Int = 0

  def run(): Unit = {

    System.out.println("Hello LWJGL " + Sys.getVersion + "!")
    System.out.println("OS: " + System.getProperty("os.name"))
    System.out.println("OS Version: " + System.getProperty("os.version"))
    System.out.println("LWJGL Version: " + org.lwjgl.Sys.getVersion)

    try {
      val window = createOpenglWindow()
      System.out.println("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION))
      loadShaders()
      GL11.glEnable(GL11.GL_TEXTURE_2D)
      GL11.glEnable(GL11.GL_DEPTH_TEST)
      GL11.glDepthFunc(GL11.GL_LESS)

      GL11.glEnable(GL11.GL_BLEND)
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      loadTexture()
      loadCube()
      //loadPyramid()
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
    val texLocation = GL20.glGetUniformLocation(program, "tex")

    val camLocation = GL20.glGetUniformLocation(program, "camera")
    val camera = Transformations.getViewTransformation(Vector3f(3.0f, 3.0f, 3.0f), Vector3f(0.0f, 0.0f, 0.0f), Vector3f(0.0f, 1.0f, 0.0f))
    val camBuffer: FloatBuffer = Matrix4f.getFloatBuffer(camera)
    GL20.glUniformMatrix4fv(camLocation, false, camBuffer)

    val projLocation = GL20.glGetUniformLocation(program, "projection")
    val persp = Transformations.getProjectionTransformation(45.0f, WIDTH, HEIGHT, 0.1f, 100.0f)
    val perspBuffer: FloatBuffer = Matrix4f.getFloatBuffer(persp)
    GL20.glUniformMatrix4fv(projLocation, false, perspBuffer)

    GL11.glEnable(GL11.GL_TEXTURE_2D)
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT |  GL11.GL_DEPTH_BUFFER_BIT)
      scale += 0.5f

      val translate = Transformations.translate(0.0f, 0.0f, 0.0f)
      val rotate = Transformations.rotate(scale, 0.0f, 1.0f, 0.0f)
      val scaling = Transformations.scale(1.0f, 1.0f, 1.0f)

      val modelLocation = GL20.glGetUniformLocation(program, "model")
      val model = Transformations.getModelTransformation(translate, rotate, scaling)
      val worldBuffer: FloatBuffer = Matrix4f.getFloatBuffer(model)
      GL20.glUniformMatrix4fv(modelLocation, false, worldBuffer)

      GL13.glActiveTexture(GL13.GL_TEXTURE0)
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, loadTexture())
      GL20.glUniform1i(texLocation, 0)

      GL30.glBindVertexArray(gVAO)
      GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6 * 2 * 3)
      GL30.glBindVertexArray(0)

      glfwSwapBuffers(window)
      glfwPollEvents()
    }

    GL20.glDeleteProgram(program)
    //GL20.glDeleteShader(fs)
    //GL20.glDeleteShader(vs)
    GL15.glDeleteBuffers(gVBO)
    GL30.glDeleteVertexArrays(gVAO)
  }

  def loadCube(): Unit = {
    //make and bind the Vertex Array Object (object that tells the OpenGL what type of data the VBO holds)
    gVAO = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(gVAO)

    //create Vertex Buffer Object that will hold information about vertices
    //here it holds the position and color of each vertex
    gVBO = GL15.glGenBuffers()
    GL15.glBindBuffer(GL_ARRAY_BUFFER, gVBO)

    val verts: FloatBuffer = BufferUtils.createFloatBuffer(30 * 6)
    val v = Array( // bottom
      -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
      1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
      -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
      1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
      1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
      -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,

      // top
      -1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
      -1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
      1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
      1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
      -1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
      1.0f, 1.0f, 1.0f,   1.0f, 1.0f,

      // front
      -1.0f,-1.0f, 1.0f,   1.0f, 0.0f,
      1.0f,-1.0f, 1.0f,   0.0f, 0.0f,
      -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,
      1.0f,-1.0f, 1.0f,   0.0f, 0.0f,
      1.0f, 1.0f, 1.0f,   0.0f, 1.0f,
      -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,

      // back
      -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
      -1.0f, 1.0f,-1.0f,   0.0f, 1.0f,
      1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
      1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
      -1.0f, 1.0f,-1.0f,   0.0f, 1.0f,
      1.0f, 1.0f,-1.0f,   1.0f, 1.0f,

      // left
      -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
      -1.0f, 1.0f,-1.0f,   1.0f, 0.0f,
      -1.0f,-1.0f,-1.0f,   0.0f, 0.0f,
      -1.0f,-1.0f, 1.0f,   0.0f, 1.0f,
      -1.0f, 1.0f, 1.0f,   1.0f, 1.0f,
      -1.0f, 1.0f,-1.0f,   1.0f, 0.0f,

      // right
      1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
      1.0f,-1.0f,-1.0f,   1.0f, 0.0f,
      1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
      1.0f,-1.0f, 1.0f,   1.0f, 1.0f,
      1.0f, 1.0f,-1.0f,   0.0f, 0.0f,
      1.0f, 1.0f, 1.0f,   0.0f, 1.0f
    )
    verts.put(v)
    verts.flip()
    GL15.glBufferData(GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW)

    // connect the xyz to the "vert" attribute of the vertex shader

    val posAttrib = GL20.glGetAttribLocation(program, "position")
    GL20.glEnableVertexAttribArray(posAttrib)
    println("posAttrib = " +  posAttrib)
    GL20.glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * 4, 0)


    val texAttrib = GL20.glGetAttribLocation(program, "vertexUV")
    // connect the uv coords to the "vertTexCoord" attribute of the vertex shader
    GL20.glEnableVertexAttribArray(texAttrib)
    println("texAttrib = " + texAttrib)
    GL20.glVertexAttribPointer(texAttrib, 2, GL_FLOAT, true,  5 * 4, 3 * 4)

    // unbind the VAO
    GL30.glBindVertexArray(0)

  }

  def loadShaders(): Unit = {
    val vs = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    val fs = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    program = compileShaderProgram(vs, fs)
    glUseProgram(program)
  }

  def loadTexture(): Int = {
    try {
      val texture = Texture.loadPNGTexture(imagePath, GL13.GL_TEXTURE0)

      texture
    } catch {
      case ex: Exception => throw ex
    }

  }

}





object Rope {
  def main(args: Array[String]) = {
    println("Up and running!")
    val rope = new Rope()
    rope.run()
  }

}