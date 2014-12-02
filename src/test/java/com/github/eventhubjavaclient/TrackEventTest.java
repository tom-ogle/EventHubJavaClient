package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class TrackEventTest extends EventHubClientTestBase {

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    mockClientResponse(200,SOME_STRING);
    makeClientRequest();
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    mockClientResponse(500,SOME_STRING);
    makeClientRequest();
  }

  public void makeClientRequest() throws UnexpectedResponseCodeException {
  }
}
