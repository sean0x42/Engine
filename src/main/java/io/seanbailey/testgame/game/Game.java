package io.seanbailey.testgame.game;

import io.seanbailey.testgame.Renderer;
import io.seanbailey.testgame.Window;

/**
 * An interface to the actual game logic.
 * @author Sean Bailey
 */
public abstract class Game {

  private final Renderer renderer;

  /**
   * Constructs a new game.
   */
  public Game() {
    this.renderer = new Renderer();
  }

  /**
   * Performs any necessary initialisation for the game logic.
   */
  public void init() {
    renderer.init();
  }

  /**
   * Handle any player input.
   * @param window Window that input originated from.
   */
  public abstract void input(Window window);

  /**
   * Update the current game state.
   * @param interval Time interval since last update.
   */
  public abstract void update(float interval);

  /**
   * Render any necessary content to the window.
   * @param window Window to render to.
   */
  public abstract void render(Window window);

  /**
   * A method that is called whenever the game is closing to cleanly exit.
   */
  public abstract void cleanup();

  public Renderer getRenderer() {
    return renderer;
  }
}
