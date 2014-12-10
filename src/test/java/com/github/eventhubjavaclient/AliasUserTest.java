package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMockit.class)
public class AliasUserTest extends EventHubClientTestBase {

  private static final String OLD_USER_NAME = "old";
  private static final String NEW_USER_NAME = "new";

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.aliasUser(NEW_USER_NAME, OLD_USER_NAME);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    mockClientResponse(500, SOME_STRING);
    client.aliasUser(NEW_USER_NAME,OLD_USER_NAME);
  }
}
