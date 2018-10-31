package io.seanbailey.testgame;

import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

/**
 * A class responsible for rendering to the
 * @link{io.seanbailey.testgame.Window}.
 * @author Sean Bailey.
 */
public class Renderer {

  private Shader shader;

  /**
   * Initialises the renderer.
   */
  public void init() {
    shader = new ShaderController("vertex.vs", "fragment.fs");
  }

  /**
   * Clears the window.
   */
  public void clear() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
  }
}
