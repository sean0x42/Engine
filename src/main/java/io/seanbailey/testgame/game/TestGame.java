package io.seanbailey.testgame.game;

import io.seanbailey.testgame.Window;
import io.seanbailey.testgame.util.Logger;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.opengl.GL11.glViewport;

/**
 * Manages logic for the test game.
 * @author Sean Bailey
 */
public class TestGame extends Game {

  private static final Logger logger = new Logger();

  private float colour = 0.0f;
  private int direction = 0;

  /**
   * Handles input.
   */
  @Override
  public void input(Window window) {
    if (window.isKeyPressed(GLFW_KEY_UP)) {
      direction = 1;
    } else if (window.isKeyPressed(GLFW_KEY_DOWN)) {
      direction = -1;
    } else {
      direction = 0;
    }
    logger.debug("Direction: %d", direction);
  }

  @Override
  public void update(float interval) {
    colour += direction * 0.01f;

    if (colour > 1) {
      colour = 1.0f;
    } else if (colour < 0) {
      colour = 0.0f;
    }
  }
  
  @Override
  public void render(Window window) {
    if (window.isResized()) {
      glViewport(0, 0, window.getWidth(), window.getHeight());
      window.setResized(false);
    }

    window.setClearColour(colour, colour, colour, 0.0f);
    getRenderer().clear();
  }

  @Override
  public void cleanup() {

  }
}
