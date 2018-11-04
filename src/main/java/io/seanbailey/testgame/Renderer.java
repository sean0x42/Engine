package io.seanbailey.testgame;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * A class responsible for rendering to the
 * @link{io.seanbailey.testgame.Window}.
 * @author Sean Bailey.
 */
public class Renderer {

  private ShaderController shader;

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

  /**
   * Renders the given mesh to the window.
   * @param window Window to render on.
   * @param mesh Mesh to render.
   */
  public void render(Window window, Mesh mesh) {
    clear();

    // Handle window resizes
    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    shader.bind();

    // Draw the mesh
    glBindVertexArray(mesh.getVAO());
    glEnableVertexAttribArray(0);
    glDrawElements(GL_TRIANGLES, mesh.getVertexCount(), GL_UNSIGNED_INT, 0);
    
    // Restore state
    glDisableVertexAttribArray(0);
    glBindVertexArray(0);

    shader.unbind();
  }

  /**
   * Perms any final clean up operations.
   */
  public void cleanup() {
    if (shader != null) {
      shader.cleanup();
    }
  }
}
