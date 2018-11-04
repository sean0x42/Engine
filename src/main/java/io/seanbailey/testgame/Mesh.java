package io.seanbailey.testgame;

import java.nio.FloatBuffer;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * The mesh class is responsible for creating Vertex Buffer Objects (VBO) and
 * Vertex Array Objects (VAO), so that a particular element can be loaded into
 * the graphics card.
 * @author Sean Bailey
 */
public class Mesh {

  private final int VAO;
  private final int VBO;
  private final int vertexCount;

  /**
   * Constructs a new mesh.
   * @param positions An array of vertices.
   */
  public Mesh(float[] positions) {
    FloatBuffer buffer = null;

    // Wrap in a try so that we always dealloc memory
    try {
      buffer = MemoryUtil.memAllocFloat(positions.length);
      vertexCount = positions.length / 3;
      buffer.put(positions).flip();

      // Create the Vertex Array Object (VAO) and bind to it
      VAO = glGenVertexArrays();
      glBindVertexArray(VAO);

      // Create the Vertex Buffer Object (VBO) and bind to it
      VBO = glGenBuffers();
      glBindBuffer(GL_ARRAY_BUFFER, VBO);
      glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

      // Define structure of data
      // <index>, <size>, <type>, <normalized?>, <stride>, <offset>
      glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

      // Unbind the VBO
      glBindBuffer(GL_ARRAY_BUFFER, 0);

      // Unbind the VAO
      glBindVertexArray(0);
    } finally {
      if (buffer != null) {
        MemoryUtil.memFree(buffer);
      }
    }
  }

  /**
   * Frees memory used by this mesh. Always call this method to prevent memory
   * leaks.
   */
  public void cleanUp() {
    glDisableVertexAttribArray(0);

    // Delete the VBO
    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glDeleteBuffers(VBO);

    // Delete the VAO
    glBindVertexArray(0);
    glDeleteVertexArrays(VAO);
  }

  public int getVAO() {
    return VAO;
  }

  public int getVBO() {
    return VBO;
  }

  public int getVertexCount() {
    return vertexCount;
  }
}
