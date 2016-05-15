package net.tedstein.rope.graphics
import java.nio.{FloatBuffer, IntBuffer}

import com.typesafe.scalalogging.StrictLogging
import net.tedstein.rope._
import net.tedstein.rope.graphics.Shader.{compileShaderProgram, createShaderObject}
import net.tedstein.rope.physics.Input
import net.tedstein.rope.util.Stopwatch
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.{GLFWErrorCallback, GLFWKeyCallback, _}
import org.lwjgl.opengl.GL11.{GL_FALSE, GL_FLOAT, GL_TRUE, glClearColor}
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20.{GL_FRAGMENT_SHADER, GL_VERTEX_SHADER, glUseProgram}
import org.lwjgl.opengl._
import org.lwjgl.system.MemoryUtil
import org.lwjgl.{BufferUtils, Version}

import scala.collection.mutable
import scala.util.Properties
//Texture sources:
//neptune: http://nasa3d.arc.nasa.gov
//sun: http://www.nasa.gov/
//moon: http://moon.nasa.gov/



class Graphics(val universe: Universe) extends StrictLogging {
 // glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
  val errorCallback = new GLFWErrorCallback {
   override def invoke(i: Int, l: Long): Unit = {
     logger.error(l.toString)}
 }

  glfwSetErrorCallback(errorCallback)

  val keyCallback = new GLFWKeyCallback {
    override def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int): Unit = {
      if (!(0 to 1024).contains(key)) { return }

      (key, action) match {
        case (GLFW_KEY_ESCAPE, GLFW_PRESS) =>
          glfwSetWindowShouldClose(window, GL_TRUE)
        case (GLFW_KEY_X, GLFW_PRESS) =>
          wireframe = !wireframe
          toggleWireframe()
        case (_, GLFW_PRESS) =>
          keys(key) = true
        case (_, GLFW_RELEASE) =>
          keys(key) = false
        case _ =>
      }
    }
  }
  val resizeCallback = new GLFWWindowSizeCallback {
    override def invoke(window: Long, width: Int, height: Int): Unit = {
      WIDTH = width
      HEIGHT = height
    }
  }
  var keys = Array.ofDim[Boolean](1024)

  val ShaderRoot = "./src/net/tedstein/rope/graphics/shaders/"
  var vertexPath = ShaderRoot + "vertex.shader"
  var fragmentPath = ShaderRoot + "fragment.shader"
  var antialiasingPath = ShaderRoot + "antialiasing.fs"

  var vertexShader = 0
  var fragmentShader = 0

  val fontString = "./assets/OpenSans-Bold.ttf"
  var fontTex = 0

  var WIDTH = 800
  var HEIGHT = 600
  var gVAO = 0
  var gVBO = 0
  var gEBO = 0
  val FLOATSIZE = 4
  val VERTEXSIZE = 3
  val TEXSIZE = 2
  var textures = Map[Texture, Int]()

  val gCamera = Camera(position = Vector3f(0.0f, 0.0f, 3.0f))
  var deltaTime = 0.0f
  var lastFrame = 0.0f
  var wireframe = false

  def run(): Unit = {
    Thread.currentThread.setName("graphics")
    val graphicsStartup = new Stopwatch("graphics setup")
    logger.info("Hello LWJGL " + Version.getVersion + "!")
    logger.info("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"))
    logger.info("Scala version: " + Properties.scalaPropOrElse("version.number", "unknown"))
    logger.info("Java version: " + System.getProperty("java.version"))
    logger.info("LWJGL Version: " + org.lwjgl.Version.getVersion)
    try {
      val window = createOpenglWindow()

      logger.info("OpenGL Version: " + GL11.glGetString(GL11.GL_VERSION))
      graphicsStartup.lap("system surveyed")
      GL11.glEnable(GL13.GL_MULTISAMPLE)
      GL11.glEnable(GL11.GL_DEPTH_TEST)
      GL11.glDepthFunc(GL11.GL_LESS)
      GL11.glEnable(GL11.GL_BLEND)
      GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
      GL11.glEnable(GL11.GL_TEXTURE_2D)
      GL11.glCullFace(GL11.GL_BACK)
      GL11.glEnable(GL11.GL_CULL_FACE)
      graphicsStartup.lap("OpenGL ready")


      val program = loadShaders(vertexPath, fragmentPath)
      graphicsStartup.lap("shaders loaded")

      val images = Texture.AllTextures.map(tex => tex -> TextureLoader.loadImage(tex.name)).toMap
      textures = images.flatMap { case (tex, img) =>
        img match {
          case Some(_) =>
            Some(tex, TextureLoader.loadTexture(img.get))
          case None =>
            logger.error(s"${tex.name} was in AllTextures, but we couldn't load it!")
            None
        }
      }

    //  fontTex = TextRenderer.InitFont(fontString)

      graphicsStartup.lap("textures loaded")


      logger.info(graphicsStartup.toString)
      renderScene(window, program)
    } finally {
      glfwTerminate()
      errorCallback.release()
    }
  }

  def createOpenglWindow(): Long = {
    if (glfwInit() != GL11.GL_TRUE)
      throw new IllegalStateException("Unable to initialize GLFW")

    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)
    glfwWindowHint(GLFW_SAMPLES, 4)

    val window: Long = glfwCreateWindow(WIDTH, HEIGHT, "Rope", MemoryUtil.NULL, MemoryUtil.NULL)

    if (window < 0)
      throw new RuntimeException("Failed to create the GLFW window")

    glfwMakeContextCurrent(window)

    val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    glfwSetWindowPos(
      window,
      (vidmode.width() - WIDTH) / 2,
      (vidmode.height() - HEIGHT) / 2
    )

    glfwMakeContextCurrent(window)
    glfwSetErrorCallback(errorCallback)
    glfwSetKeyCallback(window, keyCallback)
    glfwSetWindowSizeCallback(window, resizeCallback)
    glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL)
    glfwShowWindow(window)
    GL.createCapabilities()

    window
  }

  def renderScene(window: Long, program: Int): Unit = {

    glfwSetKeyCallback(window, keyCallback)

    //set uniform values
    val modelLocation = GL20.glGetUniformLocation(program, "model")
    val camLocation = GL20.glGetUniformLocation(program, "camera")
    val projLocation = GL20.glGetUniformLocation(program, "projection")

    glUseProgram(program)

    GL13.glActiveTexture(GL13.GL_TEXTURE0)

    val loadedModels = mutable.Map[Mesh, Int]()
    glClearColor(0.0f, 0.0f, 0.0f, 1.0f)

    while (glfwGetKey(window, GLFW_KEY_ESCAPE) != GLFW_PRESS && glfwWindowShouldClose(window) == GL_FALSE) {
      val currentFrame = glfwGetTime().toFloat
      deltaTime = currentFrame - lastFrame
      lastFrame = currentFrame

      GL11.glClear(GL11.GL_COLOR_BUFFER_BIT |  GL11.GL_DEPTH_BUFFER_BIT)

      val persp = Transformations.perspective(45.0f, WIDTH, HEIGHT, 0.1f, 100.0f)
      val perspBuffer: FloatBuffer = Matrix4f.getFloatBuffer(persp)
      GL20.glUniformMatrix4fv(projLocation, false, perspBuffer)

      val camera = gCamera.lookAt()
      val camBuffer: FloatBuffer = Matrix4f.getFloatBuffer(camera)
      GL20.glUniformMatrix4fv(camLocation, false, camBuffer)

      gCamera.updateCameraVectors(universe.player)
      for (body <- universe.bodies) {
        gVAO = loadedModels.get(body.mesh) match {
          case Some(vao) => vao
          case None =>
            val vao = body.mesh.loadMesh()
            loadedModels.put(body.mesh, vao)
            vao
        }

        var modelMat = Matrix4f()
        modelMat = Transformations.translate(modelMat, body.pos.x.toFloat, body.pos.y.toFloat, body.pos.z.toFloat)
        modelMat = Transformations.scale(modelMat, body.radius.toFloat, body.radius.toFloat, body.radius.toFloat)
        val worldBuffer: FloatBuffer = Matrix4f.getFloatBuffer(modelMat)

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textures(body.texture))
        GL30.glBindVertexArray(gVAO)
        GL20.glUniformMatrix4fv(modelLocation, false, worldBuffer)

        GL11.glDrawElements(GL11.GL_TRIANGLES, body.mesh.eboIndicesLength , GL11.GL_UNSIGNED_INT,  0)
      }

      GL15.glBindBuffer(GL_ARRAY_BUFFER, 0)
      GL30.glBindVertexArray(0)
      GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0)
      GL20.glDeleteProgram(program)
      glfwSwapBuffers(window)
      glfwPollEvents()
      captureKeys()
    }

    GL20.glDeleteProgram(program)
    GL20.glDeleteShader(fragmentShader)
    GL20.glDeleteShader(vertexShader)
    GL15.glDeleteBuffers(gVBO)
    GL30.glDeleteVertexArrays(gVAO)
  }

  def captureKeys(): Unit = {
    Input.update(
      forward = keys(GLFW_KEY_W),
      backward = keys(GLFW_KEY_S),
      strafeLeft = keys(GLFW_KEY_LEFT) || keys(GLFW_KEY_A),
      strafeRight = keys(GLFW_KEY_RIGHT) || keys(GLFW_KEY_D),
      yawLeft = keys(GLFW_KEY_Q),
      yawRight = keys(GLFW_KEY_E),
      rise = keys(GLFW_KEY_UP),
      fall = keys(GLFW_KEY_DOWN),
      slowDown = keys(GLFW_KEY_SPACE)
    )
  }

  def loadPyramid(): Unit = {
    //make and bind the Vertex Array Object (object that tells the OpenGL what type of data the VBO holds)
    gVAO = GL30.glGenVertexArrays()
    GL30.glBindVertexArray(gVAO)

    //create Vertex Buffer Object that will hold information about vertices
    //here it holds the position and color of each vertex
    gVBO = GL15.glGenBuffers()
    GL15.glBindBuffer(GL_ARRAY_BUFFER, gVBO)

    val verts: FloatBuffer = BufferUtils.createFloatBuffer(VERTEXSIZE * 4 + TEXSIZE * 4)
    val v = Array( // bottom
      -1.0f, -1.0f, 0.5773f,   0.0f, 0.0f,
      0.0f, -1.0f, -1.15475f,   1.0f, 0.0f,
      1.0f,-1.0f, 0.5773f,   0.0f, 1.0f,
      0.0f, 1.0f, 0.0f,   1.0f, 0.0f)

    verts.put(v)
    verts.flip()
    GL15.glBufferData(GL_ARRAY_BUFFER, verts, GL15.GL_STATIC_DRAW)
    // connect the xyz to the "vert" attribute of the vertex shader

    gEBO = glGenBuffers()
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, gEBO)

    val indices: IntBuffer = BufferUtils.createIntBuffer(12)
    val i = Array( 0, 3, 1,
                    1, 3, 2,
                    2, 3, 0,
                    0, 1, 2)
    indices.put(i)
    indices.flip()
    GL15.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

    val posAttrib = 0
    GL20.glEnableVertexAttribArray(posAttrib)
    GL20.glVertexAttribPointer(posAttrib, 3, GL_FLOAT, false, 5 * 4, 0)


    val texAttrib = 1
    // connect the uv coords to the "vertTexCoord" attribute of the vertex shader
    GL20.glEnableVertexAttribArray(texAttrib)
    GL20.glVertexAttribPointer(texAttrib, 2, GL_FLOAT, true,  5 * 4, 3 * 4)

    // unbind the VAO
    GL30.glBindVertexArray(0)
  }

  def loadShaders(vertexPath: String, fragmentPath: String): Int = {
    vertexShader = createShaderObject(GL_VERTEX_SHADER, vertexPath)
    fragmentShader = createShaderObject(GL_FRAGMENT_SHADER, fragmentPath)
    val program = compileShaderProgram(vertexShader, fragmentShader)
    program
  }

  def loadTexture(name: String): Int = {
    val image = TextureLoader.loadImage(name).get
    val textureID = TextureLoader.loadTexture(image)
    textureID
  }

  def toggleWireframe(): Unit = {
    if (wireframe) {
      GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
    } else {
      GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL)
    }
  }

}
