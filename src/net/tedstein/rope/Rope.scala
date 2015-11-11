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
      renderPolygon(window)
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

  def renderScene(window: Long, vertexArrayID: Int, vboiId: Int) {

    glClearColor(0.0f, 0.0f, 0.4f, 0.0f)

    val vs = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    val fs = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    val program = compileShaderProgram(vs, fs)

    var scale: Float = 0.0f
    val world = Matrix4f.setIdentity()

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT)
      glUseProgram(program)
      scale += 0.001f

      Matrix4f.setRowColumnValue(world, 0, 0, math.cos(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 1, 1, math.cos(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 1, 0, math.sin(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 0, 1, -math.sin(scale).toFloat)


      val gWorldLocation = GL20.glGetUniformLocation(program, "gWorld")

      val worldBuffer = Matrix4f.getFloatBuffer(world)

      GL20.glUniformMatrix4fv(gWorldLocation, true, worldBuffer)

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
    val vertices: FloatBuffer = BufferUtils.createFloatBuffer(24 * 4)
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


    val colors: FloatBuffer = BufferUtils.createFloatBuffer(24 * 4)
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

    (vertexArrayID, vboIndexId)
  }

  def createVBO(): Int = {
    val vbuffer = BufferUtils.createFloatBuffer(4*3)

    vbuffer.put(-1.0f).put(-1.0f).put(0.0f)
    vbuffer.put(0.0f).put(-1.0f).put(1.0f)
    vbuffer.put(1.0f).put(-1.0f).put(0.0f)
    vbuffer.put(0.0f).put(1.0f).put(0.0f)
    vbuffer.flip()

    val VBOpyramid = GL15.glGenBuffers()
    GL15.glBindBuffer(GL_ARRAY_BUFFER, VBOpyramid)
    GL15.glBufferData(GL_ARRAY_BUFFER, vbuffer, GL15.GL_STATIC_DRAW)
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
    VBOpyramid
  }

  def createIBO(): Int = {
    val ibuffer = BufferUtils.createIntBuffer(12)
    ibuffer.put(0).put(3).put(1).put(1).put(3).put(2).put(2).put(3).put(0).put(0).put(1).put(2)
    ibuffer.flip()

    val IBOpyramid = GL15.glGenBuffers()
    GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, IBOpyramid)
    GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, ibuffer, GL15.GL_STATIC_DRAW)
    IBOpyramid

  }

  def renderWithPyramid(window: Long): Unit = {
    val vs = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    val fs = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    val program = compileShaderProgram(vs, fs)

    glUseProgram(program)

    val vao: Int = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(vao)

    val vbo = createVBO()
    val ibo = createIBO()

    println(vbo)
    println(ibo)

    val World = Matrix4f.setIdentity()
    var Scale: Float = 1.0f

    val posAttrib = GL20.glGetAttribLocation(program, "Position")
    GL20.glEnableVertexAttribArray(posAttrib)
    GL20.glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 0, 0)

    val gWorldLocation = GL20.glGetUniformLocation(program, "gWorld")
    println("gWorldLocation " + gWorldLocation + GL11.glGetError())

    glClearColor(0.0f, 0.0f, 0.4f, 0.0f)
    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT)
      Scale += 0.01f

      println("error after glEnableVertexAttribArray " + GL11.glGetError())
      Matrix4f.setRowColumnValue(World, 0, 1, 0.0f)
      Matrix4f.setRowColumnValue(World, 0, 2, 0.0f)
      Matrix4f.setRowColumnValue(World, 0, 3, 0.0f)
      Matrix4f.setRowColumnValue(World, 1, 2, 0.0f)
      Matrix4f.setRowColumnValue(World, 1, 3, 0.0f)
      Matrix4f.setRowColumnValue(World, 2, 1, 0.0f)
      Matrix4f.setRowColumnValue(World, 2, 3, 0.0f)
      Matrix4f.setRowColumnValue(World, 3, 0, 0.0f)
      Matrix4f.setRowColumnValue(World, 3, 1, 0.0f)
      Matrix4f.setRowColumnValue(World, 3, 2, 0.0f)

      Matrix4f.setRowColumnValue(World, 0, 0, Math.cos(Scale).toFloat)
      Matrix4f.setRowColumnValue(World, 1, 1, 1.0f)
      Matrix4f.setRowColumnValue(World, 0, 2, -Math.sin(Scale).toFloat)
      Matrix4f.setRowColumnValue(World, 2, 0, Math.sin(Scale).toFloat)
      Matrix4f.setRowColumnValue(World, 2, 2, Math.cos(Scale).toFloat)

      val worldBuffer = Matrix4f.getFloatBuffer(World)
      worldBuffer.flip()

      println("error before getUniformMatrix4fv: " + GL11.glGetError())
      GL20.glUniformMatrix4fv(gWorldLocation, true, worldBuffer)
      println(gWorldLocation)

      println("error after getUniformMatrix4fv: " + GL11.glGetError())
      println("gworld " + gWorldLocation)

      println("error after EnableVertexAttribArray: " + GL11.glGetError())
      glBindBuffer(GL_ARRAY_BUFFER, vbo)
      GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)
      glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ibo)

      println("error after glVertexAttribPointer: " + GL11.glGetError())
      GL11.glDrawElements(GL11.GL_TRIANGLES, 12, GL11.GL_UNSIGNED_INT, 0)

      GL20.glDisableVertexAttribArray(0)
      glfwSwapBuffers(window)
      glfwPollEvents()
    }

  }

  def renderPolygon(window: Long): Unit = {
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

      val world = Matrix4f.setIdentity()
      Matrix4f.setRowColumnValue(world, 0, 0, Math.cos(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 0, 2, -Math.sin(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 2, 0, Math.sin(scale).toFloat)
      Matrix4f.setRowColumnValue(world, 2, 2, Math.cos(scale).toFloat)
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