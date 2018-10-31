package io.seanbailey.testgame;

import io.seanbailey.testgame.game.Game;
import io.seanbailey.testgame.game.TestGame;
import io.seanbailey.testgame.util.Logger;

/**
 * Main entry point to the game.
 * @author Sean Bailey
 */
public class Main {

  private static final Logger logger = new Logger();
  private long window; // Window handle
 
  /**
   * Runs the game.
   * @param args An array of command line arguments.
   */
  public static void main(String[] args) {
    logger.info("Starting test game...");
    Game game = new TestGame();
    Engine engine = new Engine("Test game", 600, 480, true, game);
    engine.start();
  }
}
