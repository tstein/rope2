package net.tedstein.rope
//rubamerza blorp

import java.nio.IntBuffer

import org.lwjgl.Sys
import org.lwjgl.opengl.{GLContext, GL11}
import org.lwjgl.opengl.GL11.{glClearColor, glClear}
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.glfw._
import org.lwjgl.system.glfw.GLFW.{glfwInit, glfwTerminate, glfwCreateWindow, glfwDestroyWindow, glfwSetWindowPos}
import org.lwjgl.system.glfw.GLFW.{glfwSetErrorCallback, glfwWindowShouldClose, glfwDefaultWindowHints, glfwWindowHint, glfwGetVideoMode}
import org.lwjgl.system.glfw.GLFW.{glfwMakeContextCurrent, glfwSwapInterval, glfwShowWindow, glfwGetPrimaryMonitor, glfwSetWindowShouldClose}
import org.lwjgl.system.glfw.GLFW.{glfwSwapBuffers, glfwPollEvents, glfwGetFramebufferSize}
import org.lwjgl.system.glfw.GLFW.{GLFW_RESIZABLE, GLFW_VISIBLE, GLFW_KEY_ESCAPE, GLFW_RELEASE, GLFW_KEY_A}
import org.lwjgl.opengl.GL11.{GL_TRUE, GL_FALSE, GL_COLOR_BUFFER_BIT, GL_DEPTH_BUFFER_BIT}


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
    println("before create window")

    val window: Long = glfwCreateWindow(WIDTH, HEIGHT, "HELLO WORLD", MemoryUtil.NULL, MemoryUtil.NULL)

    println("after create window")
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
    while (glfwWindowShouldClose(window) == GL_FALSE) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
      glfwSwapBuffers(window)
      glfwPollEvents()
    }
  }

}
