package com.github.eventhubjavaclient.exception;

/**
 * Thrown when bad input is provided to a method
 */
public class IllegalInputException extends Exception {

  public IllegalInputException(final String s) {
    super(s);
  }
}
