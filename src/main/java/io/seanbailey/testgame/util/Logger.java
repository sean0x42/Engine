package io.seanbailey.testgame.util;

import java.io.PrintStream;

/**
 * A utility class for logging to a print stream.
 * @author Sean Bailey
 */
public class Logger {

  // Whether debug messages should be printed.
  public static final boolean ENABLE_DEBUG = true;

  private PrintStream out;
  private PrintStream err;

  /**
   * Constructs a new logger.
   */
  public Logger() {
    this(System.out, System.err);
  }

  /**
   * Constructs a new logger.
   * @param out Standard output stream.
   * @param err Error output stream.
   */
  public Logger(PrintStream out, PrintStream err) {
    this.out = out;
    this.err = err;
  }

  /**
   * Print a regular message.
   * @param message Message to print.
   */
  public void info(String message) {
    print(out, message);
  }

  /**
   * Print a regular, formatted message.
   * @param format Message format.
   * @param values Values to use in format string.
   */
  public void info(String format, Object... values) {
    print(out, String.format(format, values));
  }

  /**
   * Prints a debug message.
   * @param message Message to print.
   */
  public void debug(String message) {
    // Ensure that debug messages are enabled in this build
    if (!ENABLE_DEBUG) {
      return;
    }

    print(out, "Debug: " + message);
  }

  /**
   * Prints a formatted debug message.
   * @param format Message format.
   * @param values Values to use in format string.
   */
  public void debug(String format, Object... values) {
    debug(String.format(format, values));
  }

  /**
   * Prints a warning message.
   * @param message Message to print.
   */
  public void warning(String message) {
    print(out, "Warning: " + message);
  }

  /**
   * Prints a warning message.
   * @param format Message format.
   * @param values Values to use in format string.
   */
  public void warning(String format, Object... values) {
    warning(String.format(format, values));
  }

  /**
   * Print an error message.
   * @param message Message to print.
   */
  public void error(String message) {
    print(err, "Error: " + message);
  }

  /**
   * Print a formatted error message.
   * @param format Message format.
   * @param values Values to use in format string.
   */
  public void error(String format, Object... values) {
    error(String.format(format, values));
  }

  /**
   * Prints to a target stream.
   * @param stream Stream to print to.
   * @param message Message to print.
   */
  private void print(PrintStream stream, String message) {
    stream.println(message);
  }
}

