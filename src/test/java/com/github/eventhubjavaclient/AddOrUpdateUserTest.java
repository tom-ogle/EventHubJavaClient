package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
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

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    Map<String, String> properties = createPropertiesMap();
    mockClientResponse(500, SOME_STRING);
    client.addOrUpdateUser(USER_ID,properties);
  }

  private static Map<String, String> createPropertiesMap() {
    Map<String, String> map = new HashMap<String, String>();
    map.put("property1","value1");
    map.put("property2","value2");
    return map;
  }

}
