package io.seanbailey.testgame;

import io.seanbailey.testgame.util.Logger;
import io.seanbailey.testgame.util.ResourceUtils;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

/**
 * A tool for creating and linking OpenGL shaders.
 * @author Sean Bailey
 */
public class ShaderController {

  private static final Logger logger = new Logger();

  private final int programId;
  private int vertexShaderId;
  private int fragmentShaderId;

  /**
   * Constructs a new shader.
   * @param vertexPath Path to vertex shader.
   * @param fragmentPath Path to fragment shader.
   * @throws Exception if the shader cannot be created.
   */
  public ShaderController(String vertexPath, String fragmentPath) {
    programId = glCreateProgram();

    // Ensure program was created
    if (programId == 0) {
      throw new RuntimeException("Could not create shader");
    }

    // Create shaders
    try {
      createVertexShader(ResourceUtils.loadAsString("/" + vertexPath));
      createFragmentShader(ResourceUtils.loadAsString("/" + fragmentPath));
    } catch (IOException exception) {
      exception.printStackTrace();
      throw new RuntimeException("IO exception encountered");
    }

    link();
  }

  /**
   * Creates a new vertex shader.
   * @param shader GLSL shader code.
   */
  private void createVertexShader(String shader) {
    vertexShaderId = createShader(shader, GL_VERTEX_SHADER);
  }

  /**
   * Creates a new fragment shader.
   * @param shader GLSL shader code.
   */
  private void createFragmentShader(String shader) {
    fragmentShaderId = createShader(shader, GL_FRAGMENT_SHADER);
  }

  /**
   * Creates a generic shader, returning it's id.
   * @param shader GLSL shader code.
   * @param shaderType The OpenGL shader type.
   * @return Shader id.
   */
  private int createShader(String shader, int shaderType) {
    // Init
    int shaderId = glCreateShader(shaderType);
    if (shaderId == 0) {
      throw new RuntimeException("Error creating shader of type " + shaderType);
    }

    // Compile
    glShaderSource(shaderId, shader);
    glCompileShader(shaderId);

    // Ensure compilation was successful
    if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
      throw new RuntimeException("Error compiling shaders: " +
          glGetShaderInfoLog(shaderId, 1024));
    }

    glAttachShader(programId, shaderId);
    return shaderId;
  }

  /**
   * Links some shaders or something... idk
   */
  private void link() {
    // Attempt to link
    glLinkProgram(programId);
    if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
      throw new RuntimeException("Error linking shader code: " +
          glGetProgramInfoLog(programId, 1024));
    }

    if (vertexShaderId != 0) {
      glDetachShader(programId, vertexShaderId);
    }

    if (fragmentShaderId != 0) {
      glDetachShader(programId, fragmentShaderId);
    }

    glValidateProgram(programId);
    if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
      logger.warning("Whilst validating shader code: " +
          glGetProgramInfoLog(programId, 1024));
    }
  }

  public void bind() {
    glUseProgram(programId);
  }

  public void unbind() {
    glUseProgram(0);
  }

  public void cleanup() {
    unbind();
    if (programId != 0) {
      glDeleteProgram(programId);
    }
  }
}
