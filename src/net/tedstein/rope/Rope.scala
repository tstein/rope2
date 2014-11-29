package net.tedstein.rope
//rubamerza blorp

import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.{BufferUtils, Sys}
import org.lwjgl.opengl.{GLContext, GL11}
import org.lwjgl.opengl.GL11.{glClearColor, glDrawArrays, glDisableClientState}
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.glfw._
import org.lwjgl.system.glfw.GLFW.{glfwInit, glfwTerminate, glfwCreateWindow, glfwDestroyWindow, glfwSetWindowPos}
import org.lwjgl.system.glfw.GLFW.{glfwSetErrorCallback, glfwWindowShouldClose, glfwDefaultWindowHints, glfwWindowHint, glfwGetVideoMode}
import org.lwjgl.system.glfw.GLFW.{glfwMakeContextCurrent, glfwSwapInterval, glfwShowWindow, glfwGetPrimaryMonitor, glfwSetWindowShouldClose}
import org.lwjgl.system.glfw.GLFW.{glfwSwapBuffers, glfwPollEvents, glfwGetFramebufferSize, glfwGetTime}
import org.lwjgl.opengl.GL11.{glViewport, glEnable, glClear, glMatrixMode, glLoadIdentity, glTranslatef, glRotatef, glScalef, glColor3f}
import org.lwjgl.opengl.GL11.{glVertex3f, glBegin, glEnd, glEnableClientState, glVertexPointer, glColorPointer}
import org.lwjgl.opengl.GL11.{GL_TRUE, GL_FALSE, GL_DEPTH_TEST, GL_FLOAT, GL_POLYGON, GL_VERTEX_ARRAY, GL_COLOR_ARRAY, GL_TRIANGLES, GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT, GL_PROJECTION, GL_MODELVIEW}
import org.lwjgl.system.glfw.GLFW.{GLFW_RESIZABLE, GLFW_VISIBLE, GLFW_KEY_ESCAPE, GLFW_RELEASE}

object Rope {

  def main(args: Array[String]) = {
    println("Up and running!")
    createDisplay()
  }

  def createDisplay(): Unit = {
    System.out.println("Hello LWJGL " + Sys.getVersion() + "!")
    try {
      val window: Long = init()
      loop(window)
      glfwDestroyWindow(window)
    } finally {
      glfwTerminate()
    }
  }

  def init(): Long = {
    glfwSetErrorCallback(ErrorCallback.Util.getDefault())
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

    WindowCallback.set(window, new WindowCallbackAdapter() {
      override
      def key(window: Long, pressed_key: Int, scancode: Int, action: Int, mods: Int)  {
        if (pressed_key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
          glfwSetWindowShouldClose(window, GL_TRUE)
      }
    })

    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
      window,
      (GLFWvidmode.width(vidmode) - WIDTH) / 2,
      (GLFWvidmode.height(vidmode) - HEIGHT) / 2
    )

    glfwMakeContextCurrent(window)

    glfwSwapInterval(1)
    glfwShowWindow(window)

    return window
  }

  def loop(window: Long) {
    GLContext.createFromCurrent()
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f)

    //val width: IntBuffer = BufferUtils.createIntBuffer(4)
    //val height: IntBuffer = BufferUtils.createIntBuffer(4)
    glEnable(GL_DEPTH_TEST)

    while (glfwWindowShouldClose(window) == GL_FALSE) {
     // glfwGetFramebufferSize(window, width, height)
      //glViewport(0, 0, width.get(0), height.get(0))

      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)

      drawDumbCube(0, 0, 0)

      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }


  def drawCube(x: Float, y: Float, z: Float): Unit = {
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

  def drawDumbCube(x: Int, y: Int, z: Int): Unit = {

    glLoadIdentity()

    glTranslatef(x, y, z)
    glRotatef(glfwGetTime().toFloat * 50.0f, 1.0f, 0.0f, 0.0f)
    glRotatef(glfwGetTime().toFloat * 50.0f, 0.0f, 1.0f, 0.0f)
    glScalef(0.25f, 0.25f, 0.25f)

    glBegin(GL_POLYGON)

    glColor3f(1.0f, 0.0f, 0.0f)
    glVertex3f(0.5f, -0.5f, -0.5f)    // P1 is red
    glColor3f(0.0f, 1.0f, 0.0f)
    glVertex3f(0.5f,  0.5f, -0.5f)      // P2 is green
    glColor3f(0.0f, 0.0f, 1.0f)
    glVertex3f(-0.5f,  0.5f, -0.5f)     // P3 is blue
    glColor3f(1.0f, 0.0f, 1.0f)
    glVertex3f(-0.5f, -0.5f, -0.5f)      // P4 is purple

    glEnd()

    // White side - BACK
    glBegin(GL_POLYGON)
    glColor3f(1.0f,  1.0f, 1.0f)
    glVertex3f(0.5f, -0.5f, 0.5f)
    glVertex3f(0.5f,  0.5f, 0.5f)
    glVertex3f(-0.5f,  0.5f, 0.5f)
    glVertex3f(-0.5f, -0.5f, 0.5f)
    glEnd() 

    // Purple side - RIGHT
    glBegin(GL_POLYGON) 
    glColor3f(1.0f,  0.0f,  1.0f)
    glVertex3f(0.5f, -0.5f, -0.5f)
    glVertex3f(0.5f,  0.5f, -0.5f)
    glVertex3f(0.5f,  0.5f,  0.5f)
    glVertex3f(0.5f, -0.5f,  0.5f)
    glEnd() 

    // Green side - LEFT
    glBegin(GL_POLYGON) 
    glColor3f(0.0f,  1.0f,  0.0f)
    glVertex3f(-0.5f, -0.5f,  0.5f)
    glVertex3f(-0.5f,  0.5f,  0.5f)
    glVertex3f(-0.5f,  0.5f, -0.5f)
    glVertex3f(-0.5f, -0.5f, -0.5f)
    glEnd() 

    // Blue side - TOP
    glBegin(GL_POLYGON) 
    glColor3f(0.0f,  0.0f,  1.0f)
    glVertex3f(0.5f,  0.5f,  0.5f)
    glVertex3f(0.5f,  0.5f, -0.5f)
    glVertex3f(-0.5f,  0.5f, -0.5f)
    glVertex3f(-0.5f,  0.5f,  0.5f)
    glEnd() 

    // Red side - BOTTOM
    glBegin(GL_POLYGON) 
    glColor3f(1.0f,  0.0f,  0.0f)
    glVertex3f(0.5f, -0.5f, -0.5f)
    glVertex3f(0.5f, -0.5f,  0.5f)
    glVertex3f(-0.5f, -0.5f,  0.5f)
    glVertex3f(-0.5f, -0.5f, -0.5f)
    glEnd() 

  }

}
