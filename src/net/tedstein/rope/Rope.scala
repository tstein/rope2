package net.tedstein.rope
//rubamerza blorp

import java.nio.ByteBuffer

import org.lwjgl.Sys
import org.lwjgl.opengl.{GLContext, GL11}
import org.lwjgl.opengl.GL11.{glClearColor, glClear}
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.glfw._
import org.lwjgl.system.glfw.GLFW.{glfwInit, glfwTerminate, glfwCreateWindow, glfwDestroyWindow, glfwSetWindowPos}
import org.lwjgl.system.glfw.GLFW.{glfwSetErrorCallback, glfwWindowShouldClose, glfwDefaultWindowHints, glfwWindowHint, glfwGetVideoMode}
import org.lwjgl.system.glfw.GLFW.{glfwMakeContextCurrent, glfwSwapInterval, glfwShowWindow, glfwGetPrimaryMonitor, glfwSetWindowShouldClose}
import org.lwjgl.system.glfw.GLFW.{glfwSwapBuffers, glfwPollEvents}
import org.lwjgl.system.glfw.GLFW.{GLFW_RESIZABLE, GLFW_VISIBLE, GLFW_KEY_ESCAPE, GLFW_RELEASE}
import org.lwjgl.opengl.GL11.{GL_TRUE, GL_FALSE, GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT}


object Rope {

  def main(args: Array[String]) = {
    println("Up and running!")
    execute()
  }

  def execute(): Unit = {
    System.out.println("Hello LWJGL " + Sys.getVersion() + "!")
    try {
      println("WHAT THE WHAT")
      val window: Long = init()
      loop(window)
      glfwDestroyWindow(window)
    } finally {
      println("BLORP")
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
    println("before create window")
    val title: CharSequence = "Hello World"
    val monitor: java.lang.Long = null
    val share: java.lang.Long = null

    //RUBA: things are unhappy when this call is made, also the output log shows an error related to WindowCallback below
    //oh I got the MemoryUtil.NULL thing from an lwjgl online forum. Multiple people made the call using it.
    //just switch both MemoryUtils with the variables monitor and share and run it to get the java.lang.NullPointerException
    val window: Long = GLFW.glfwCreateWindow(WIDTH, HEIGHT, "HELLO WORLD", MemoryUtil.NULL, MemoryUtil.NULL)

    println("after create window")
    if (window == null)
      throw new RuntimeException("Failed to create the GLFW window")

    //RUBA: there's something wrong here, too, I think.
    /*
    Exception in thread "main" java.lang.NoClassDefFoundError: com/lmax/disruptor/WaitStrategy
    at org.lwjgl.system.glfw.WindowCallback.set(WindowCallback.java:113)
    at org.lwjgl.system.glfw.WindowCallback.set(WindowCallback.java:92)
    at org.lwjgl.system.glfw.GLFW.glfwCreateWindow(GLFW.java:1011)
    at net.tedstein.rope.Rope$.init(Rope.scala:57)
    at net.tedstein.rope.Rope$.execute(Rope.scala:31)
    at net.tedstein.rope.Rope$.main(Rope.scala:24)
    */
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
    glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glfwSwapBuffers(window)
      glfwPollEvents()
    }

  }
}
