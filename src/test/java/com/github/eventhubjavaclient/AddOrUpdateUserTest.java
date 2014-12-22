package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class AddOrUpdateUserTest extends EventHubClientTestBase {

  private static final String USER_ID = "1";

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    mockClientResponse(200,SOME_STRING);
    client.addOrUpdateUser(USER_ID,properties);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithNullMap() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.addOrUpdateUser(USER_ID,null);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithEmptyMap() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.addOrUpdateUser(USER_ID,new HashMap<String, String>());
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    mockClientResponse(500, SOME_STRING);
    client.addOrUpdateUser(USER_ID,properties);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullUserId() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    client.addOrUpdateUser(null,properties);
  }

  @Test
  public void testShouldAddQueryParamForEachMapEntry() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("external_user_id", anyString); result = webResource;
      webResource.queryParam("property1", "value1"); result = webResource; times = 1;
      webResource.queryParam("property2", "value2"); result = webResource; times = 1;
      webResource.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.addOrUpdateUser(USER_ID,properties);
  }

  @Test
  public void testShouldAddQueryParamForUserId() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("external_user_id", USER_ID); result = webResource; times = 1;
      webResource.queryParam(anyString, anyString); result = webResource;
      webResource.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.addOrUpdateUser(USER_ID,properties);
  }

  private static Map<String, String> createPropertiesMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("property1", "value1");
    map.put("property2", "value2");
    return map;
  }

}
