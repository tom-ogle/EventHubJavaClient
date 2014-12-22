package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(JMockit.class)
public class GetEventKeysTest extends EventHubClientTestBase {

  @Test
  public void testShouldAddQueryParamEventType() throws Exception {
    final String eventType = "event type";
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("event_type", eventType); result = webResource; times = 1;
      webResource.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.get(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
      response.getEntity(String.class); result = "[\n  \"foo\",\n  \"hello\"\n]\n";
    }};
    client.getEventKeys(eventType);
  }

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    mockClientResponse(200,"[\n  \"foo\",\n  \"hello\"\n]\n");

    String[] actualEventKeysArray = client.getEventKeys(SOME_STRING);

    int actualEventKeysArrayLength = actualEventKeysArray.length;
    int expectedEventKeysArrayLength = 2;
    assertTrue("Expected Event Keys array to have "+expectedEventKeysArrayLength+" entries but it had "+actualEventKeysArrayLength,actualEventKeysArrayLength==expectedEventKeysArrayLength);

    String[] expectedEventKeysArray = {"foo","hello"};
    assertArrayEquals("The returned Event Keys array was not what we expected",expectedEventKeysArray,actualEventKeysArray);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    mockClientResponse(200,"[]");

    String[] actualEventKeysArray = client.getEventKeys(SOME_STRING);

    int actualEventKeysArrayLength = actualEventKeysArray.length;
    int expectedEventKeysArrayLength = 0;
    assertTrue("Expected Event Keys array to have "+expectedEventKeysArrayLength+" entries but it had "+actualEventKeysArrayLength,actualEventKeysArrayLength==expectedEventKeysArrayLength);
  }

  @Test
  public void testShouldExtractFromGoodJsonWithQuotesResponse() throws Exception {
    mockClientResponse(200,"[\"foo\",\"hello\"]");

    String[] actualEventKeysArray = client.getEventKeys(SOME_STRING);

    int actualEventKeysArrayLength = actualEventKeysArray.length;
    int expectedEventKeysArrayLength = 2;
    assertTrue("Expected Event Keys array to have "+expectedEventKeysArrayLength+" entries but it had "+actualEventKeysArrayLength,actualEventKeysArrayLength==expectedEventKeysArrayLength);

    String[] expectedEventKeysArray = {"foo","hello"};
    assertArrayEquals("The returned Event Keys array was not what we expected",expectedEventKeysArray,actualEventKeysArray);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOn500Response() throws Exception {
    mockClientResponse(500,null);
    client.getEventKeys(SOME_STRING);
  }


  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");

    String[] actualEventKeysArray = client.getEventKeys(SOME_STRING);
    assertNull("Expected a null return value but was not null",actualEventKeysArray);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBodyObject() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody}");
    client.getEventKeys(SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");
    client.getEventKeys(SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);
    client.getEventKeys(SOME_STRING);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEventType() throws Exception {
    mockClientResponse(200,"[\"foo\",\"hello\"]");
    client.getEventKeys(null);
  }
}
