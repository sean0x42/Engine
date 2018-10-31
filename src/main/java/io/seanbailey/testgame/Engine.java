package io.seanbailey.testgame;

import io.seanbailey.testgame.game.Game;
import io.seanbailey.testgame.util.Logger;
import io.seanbailey.testgame.util.Timer;

/**
 * Game engine.
 * @author Sean Bailey
 */
public class Engine implements Runnable {

  private static final Logger logger = new Logger();
  public static final int TARGET_FPS = 75;
  public static final int TARGET_UPS = 30;

  private final Thread gameThread;
  private final Window window;
  private final Game game;
  private final Timer timer;

  /**
   * Constructs a new engine instance.
   * @param title Title to add to window.
   * @param width Initial window width.
   * @param height Initial window height.
   * @param vSync Whether vertically synchronize with the GPU.
   * @param game Game logic.
   */
  public Engine(String title, int width, int height, boolean vSync,
      Game game) {
    this.gameThread = new Thread(this, "GAME_THREAD");
    this.window = new Window(title, width, height, vSync);
    this.game = game;
    this.timer = new Timer();
  }

  /**
   * Starts any necessary threads.
   */
  public void start() {
    if (System.getProperty("os.name").contains("Mac")) {
      gameThread.run();
    } else {
      gameThread.start();
    }
  }

  /**
   * Run the engines main thread.
   */
  @Override
  public void run() {
    try {
      window.init();
      game.init();
      performEngineLoop();
    } finally {
      game.cleanup();
    }
  }

  /**
   * Runs the actual engine loop.
   */
  protected void performEngineLoop() {
    // Init
    float elapsed;
    float accumulator = 0f;
    float interval = 1f / TARGET_UPS;
    boolean running = true;

    // Loop until the application closes
    while (running && !window.shouldClose()) {
      elapsed = timer.getElapsedTime();
      accumulator += elapsed;

      // Handle input
      game.input(window);

      while (accumulator >= interval) {
        game.update(interval);
        accumulator -= interval;
      }

      game.render(window);
      window.update();

      if (window.isVerticalSyncEnabled()) {
        sync();
      }
    }
  }

  /**
   * Keeps the engine in sync with the GPU and target FPS.
   */
  private void sync() {
    float loopSlot = 1f / TARGET_FPS;
    double endTime = timer.getPreviousTime() + loopSlot;
    while (timer.getTime() < endTime) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignored) {}
    }
  }
}
