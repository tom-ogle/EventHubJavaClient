package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

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

  @Test
  public void testShouldAddQueryParamForNewUserName() throws Exception {
    final String newUserName = "new user name";
    final String oldUserName = "old user name";
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("from_external_user_id", newUserName); result = webResource; times = 1;
      webResource.queryParam("to_external_user_id", oldUserName); result = webResource;
      webResource.accept(withAny(MediaType.WILDCARD_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.aliasUser(newUserName, oldUserName);
  }

  @Test
  public void testShouldAddQueryParamForOldUserName() throws Exception {
    final String newUserName = "new user name";
    final String oldUserName = "old user name";
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("from_external_user_id", newUserName); result = webResource;
      webResource.queryParam("to_external_user_id", oldUserName); result = webResource; times = 1;
      webResource.accept(withAny(MediaType.WILDCARD_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.aliasUser(newUserName, oldUserName);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullNewUsername() throws Exception {
    mockClientResponse(200, SOME_STRING);
    client.aliasUser(null, "old username");
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullOldUsername() throws Exception {
    mockClientResponse(200, SOME_STRING);
    client.aliasUser("new username", null);
  }

}
