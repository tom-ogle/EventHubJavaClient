package com.github.eventhubjavaclient.exception;

public class BadlyFormedResponseBodyException extends Exception {
  public BadlyFormedResponseBodyException() {
  }

  public BadlyFormedResponseBodyException(final String s) {
    super(s);
  }

  public BadlyFormedResponseBodyException(final String s, final Throwable throwable) {
    super(s, throwable);
  }

  public BadlyFormedResponseBodyException(final Throwable throwable) {
    super(throwable);
  }
}
