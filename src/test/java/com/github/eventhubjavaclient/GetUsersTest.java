package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class GetUsersTest extends EventHubClientTestBase {

  private static final String ENTITY_BODY_2_VALUES = "[\n"
      + "  {\n"
      + "    \"external_user_id\": \"someemailaddress@somedomain.com\"\n"
      + "  },\n"
      + "  {\n"
      + "    \"external_user_id\": \"anotheraddress@someotherdomain.com\"\n"
      + "  }\n"
      + "]\n";
  private static final String ENTITY_BODY_NO_VALUES = "[]";
  private static final String ENTITY_BODY_BADLY_FORMED = "[";
  private static final String ENTITY_BODY_WRONG_JSON = "{}";

  private static final String USER_FILTERS_MAP_USER_ID_KEY = "external_user_id";
  private static final String EMAIL_IN_RESPONSE = "someemailaddress@somedomain.com";
  private static final String EMAIL_IN_RESPONSE2 = "anotheraddress@someotherdomain.com";

  private Map<String,String> userFilters;

  @Before
  public void before() {
    userFilters = new HashMap<String, String>();
    userFilters.put(USER_FILTERS_MAP_USER_ID_KEY, "anyemail@address.com");
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionWithEmptyFilterMap() throws Exception {
    mockClientResponse(200, ENTITY_BODY_2_VALUES);
    client.getUsers(new HashMap<String, String>());
  }

  @Test
  public void testShouldCompleteSuccessfullyWithPopulatedFilterMap() throws Exception {
    mockClientResponse(200, ENTITY_BODY_2_VALUES);
    client.getUsers(userFilters);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    mockClientResponse(500, ENTITY_BODY_2_VALUES);
    client.getUsers(userFilters);
  }

  @Test
  public void testShouldReturnEmptyListWhenEmptyListReturnedInResponse() throws Exception {
    mockClientResponse(200, ENTITY_BODY_NO_VALUES);
    List<String> usernameResults = client.getUsers(userFilters);
    assertNotNull("The user name results list was null", usernameResults);
    assertEquals("Expected username results to have no entries but it had entries", 0, usernameResults.size());
  }

  @Test
  public void testShouldExtractUsernamesWhenPopulatedListReturnedInResponse() throws Exception {

    mockClientResponse(200, ENTITY_BODY_2_VALUES);
    List<String> usernameResults = client.getUsers(userFilters);
    assertNotNull("The user name results list was null",usernameResults);
    assertEquals("Expected username results to have 2 entries",2,usernameResults.size());
    String username1 = usernameResults.get(0);
    String username2 = usernameResults.get(1);
    assertNotNull(username1);
    assertNotNull(username2);
    assertTrue(username1.equals(EMAIL_IN_RESPONSE)||username1.equals(EMAIL_IN_RESPONSE2));
    assertTrue(username2.equals(EMAIL_IN_RESPONSE)||username2.equals(EMAIL_IN_RESPONSE2));
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionWithNullFilterMap() throws Exception {
    mockClientResponse(200, ENTITY_BODY_2_VALUES);
    client.getUsers(null);
  }

  @Test
  public void testShouldPostProvidedBody() throws Exception {
    Map<String,String> userFilters = new HashMap<String, String>();
    userFilters.put(USER_FILTERS_MAP_USER_ID_KEY, EMAIL_IN_RESPONSE);
    final String expectedPostedBody = "ufk[]="+USER_FILTERS_MAP_USER_ID_KEY+"&ufv[]="+EMAIL_IN_RESPONSE;
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.header(anyString,anyString); result = builder;
      builder.post(ClientResponse.class,expectedPostedBody); result = response; times = 1;
      response.getStatus(); result = 200;
      response.getEntity(String.class); result = ENTITY_BODY_2_VALUES;
    }};
    client.getUsers(userFilters);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldForBadlyFormedJSONResponse() throws Exception {
    mockClientResponse(200, ENTITY_BODY_BADLY_FORMED);
    client.getUsers(userFilters);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldForWrongJSONFormatResponse() throws Exception {
    mockClientResponse(200, ENTITY_BODY_WRONG_JSON);
    client.getUsers(userFilters);
  }
}
