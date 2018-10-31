package io.seanbailey.testgame.util;

import java.io.InputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * A collection of utilities for accessing resources.
 * @author Sean Bailey
 */
public class ResourceUtils {

  /**
   * Loads the resource's contents as a string.
   * @param path Path to resource.
   * @return Contents of the resource as a string.
   * @throws IOException if the resource cannot be read.
   */
  public static String loadAsString(String path) throws IOException {
    String result;

    try (InputStream in = Class.forName(ResourceUtils.class.getName()).getResourceAsStream(path)) {
      Scanner scanner = new Scanner(in, "UTF-8");
      result = scanner.useDelimiter("\\A").next();
    } catch (ClassNotFoundException ignored) {
      return null;
    }

    return result;
  }
}
