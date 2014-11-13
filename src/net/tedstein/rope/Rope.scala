package net.tedstein.rope
//rubamerza blorp

import org.lwjgl.LWJGLException
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.Display
import org.lwjgl.opengl.DisplayMode


object Rope {
  def main(args: Array[String]) = {
    println("Up and running!")
    startDisplay()
  }

  def startDisplay() {
    try {
      Display.setDisplayMode(new DisplayMode(800, 600))
      Display.create()
    } catch {
      case e: LWJGLException => e.printStackTrace()
    }
     while (!Display.isCloseRequested) {
      pollInput()
      Display.update()
    }

    Display.destroy()
  }

  def pollInput() {
    if (Mouse.isButtonDown(0)) {
      val x = Mouse.getX
      val y = Mouse.getY
      System.out.println("Mouse Down @ X: " + x + "Y: " + y)
    }
    if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
      System.out.println("SPACE KEY IS DOWN")
    }
    while (Keyboard.next()) {
      if (Keyboard.getEventKeyState) {
        if (Keyboard.getEventKey == Keyboard.KEY_A) {
          System.out.println("A Key Pressed")
        }
        if (Keyboard.getEventKey == Keyboard.KEY_S) {
          System.out.println("S Key Pressed")
        }
        if (Keyboard.getEventKey == Keyboard.KEY_D) {
          System.out.println("D Key Pressed")
        }
      } else {
        if (Keyboard.getEventKey == Keyboard.KEY_A) {
          System.out.println("A Key Released")
        }
        if (Keyboard.getEventKey == Keyboard.KEY_S) {
          System.out.println("S Key Released")
        }
        if (Keyboard.getEventKey == Keyboard.KEY_D) {
          System.out.println("D Key Released")
        }
      }
    }
  }
}