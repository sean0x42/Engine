package io.seanbailey.testgame.util;

/**
 * A utility class for tracking time periods between calls.
 * @author Sean Bailey
 */
public class Timer {

  private double previousTime;

  /**
   * Constructs a new timer.
   */
  public Timer() {
    previousTime = getTime();
  }

  /**
   * Returns the amount of time that has elapsed since the last use of this
   * function.
   * @return Elapsed time as a float.
   */
  public float getElapsedTime() {
    double time = getTime();
    float elapsedTime = (float) (time - previousTime);
    previousTime = time;
    return elapsedTime;
  }

  /**
   * Gets the current system time.
   * @return System time.
   */
  public double getTime() {
    return System.nanoTime() / 1_000_000_000.0;
  }

  public double getPreviousTime() {
    return previousTime;
  }
}
