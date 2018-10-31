package io.seanbailey.testgame;

import io.seanbailey.testgame.util.Logger;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * A class for controlling the physical game window.
 * @author Sean Bailey
 */
public class Window {

  private static final Logger logger = new Logger();

  private final String title;
  private int width;
  private int height;
  private long handle;
  private boolean resized;
  private boolean verticalSync;

  /**
   * Constructs a new window.
   * @param title Window title.
   * @param width Width in pixels.
   * @param height Height in pixels.
   * @param verticalSync Whether to vertically synchronize with the GPU.
   */
  public Window(String title, int width, int height, boolean verticalSync) {
    this.title = title;
    this.width = width;
    this.height = height;
    this.verticalSync = verticalSync;
    this.resized = false;
  }

  /**
   * Initialises the window.
   */
  public void init() {
    // Setup an error callback
    GLFWErrorCallback.createPrint(System.err).set();

    // Initialise GLFW. Most GLFW functions will not work until we call this
    if (!glfwInit()) {
      throw new IllegalStateException("Unable to initialise GLFW");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

    // Create the window
    handle = glfwCreateWindow(width, height, title, NULL, NULL);
    if (handle == NULL) {
      throw new RuntimeException("Failed to create GLFW window");
    }

    // Setup resize callbacks
    glfwSetFramebufferSizeCallback(handle, (window, width, height) -> {
      this.width = width;
      this.height = height;
      this.setResized(true);
    });

    // Setup key callback
    glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
      if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
        glfwSetWindowShouldClose(window, true);
      }
    });

    // Get the resolution of primary monitor
    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

    // Centre window
    glfwSetWindowPos(
        handle, 
        (vidmode.width() - width) / 2, 
        (vidmode.height() - height) / 2
    );

    // Make the OpenGL context current
    glfwMakeContextCurrent(handle);

    // Enable v sync
    if (verticalSync) {
      glfwSwapInterval(1);
    }

    // Show window
    glfwShowWindow(handle);
    GL.createCapabilities();

    // Set clear colour
    glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    logger.info("Created window with handle %d", handle);
  }

  /**
   * Updates the current clear colour.
   * @param r Red.
   * @param g Green.
   * @param b Blue.
   * @param a Alpha (opacity).
   */
  public void setClearColour(float r, float g, float b, float a) {
    glClearColor(r, g, b, a);
  }

  /**
   * Determines whether the given key is currently being pressed.
   * @param keyCode Key to check.
   * @return Whether the given key is being pressed.
   */
  public boolean isKeyPressed(int keyCode) {
    return glfwGetKey(handle, keyCode) == GLFW_PRESS;
  }

  /**
   * Update the window.
   */
  public void update() {
    glfwSwapBuffers(handle);
    glfwPollEvents();
  }

  public void setResized(boolean resized) {
    this.resized = resized;
  }

  public boolean isResized() {
    return resized;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public boolean shouldClose() {
    return glfwWindowShouldClose(handle);
  }

  public boolean isVerticalSyncEnabled() {
    return verticalSync;
  }
}
