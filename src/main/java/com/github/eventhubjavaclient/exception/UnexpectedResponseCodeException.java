package com.github.eventhubjavaclient.exception;

/**
 *
 */
public class UnexpectedResponseCodeException extends Exception {

  private int[] expectedCodeArray;
  private int actualCode;

  public UnexpectedResponseCodeException() {
  }

  public UnexpectedResponseCodeException(final String s) {
    super(s);
  }

  public UnexpectedResponseCodeException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public UnexpectedResponseCodeException(final Throwable throwable) {
    super(throwable);
  }

  public UnexpectedResponseCodeException(final int[] expectedCodeArray, final int actualCode) {
    this.expectedCodeArray = expectedCodeArray;
    this.actualCode = actualCode;
  }

  public int[] getExpectedCodeArray() {
    return expectedCodeArray;
  }

  public void setExpectedCodeArray(final int[] expectedCodeArray) {
    this.expectedCodeArray = expectedCodeArray;
  }

  public int getActualCode() {
    return actualCode;
  }

  public void setActualCode(final int actualCode) {
    this.actualCode = actualCode;
  }
}
