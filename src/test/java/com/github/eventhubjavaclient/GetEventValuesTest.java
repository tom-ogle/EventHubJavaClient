package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class GetEventValuesTest extends EventHubClientTestBase {

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    mockClientResponse(200,"[\n  \"foo\",\n  \"hello\"\n]\n");

    String[] actualEventValuesArray = client.getEventValues(SOME_STRING,SOME_STRING);

    int actualEventValuesArrayLength = actualEventValuesArray.length;
    int expectedEventValuesArrayLength = 2;
    assertTrue("Expected Event values array to have "+expectedEventValuesArrayLength+" entries but it had "+actualEventValuesArrayLength,actualEventValuesArrayLength==expectedEventValuesArrayLength);

    String[] expectedEventValuesArray = {"foo","hello"};
    assertArrayEquals("The returned Event values array was not what we expected",expectedEventValuesArray,actualEventValuesArray);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    mockClientResponse(200,"[]");

    String[] actualEventValuesArray = client.getEventValues(SOME_STRING,SOME_STRING);

    int actualEventValuesArrayLength = actualEventValuesArray.length;
    int expectedEventValuesArrayLength = 0;
    assertTrue("Expected Event values array to have "+expectedEventValuesArrayLength+" entries but it had "+actualEventValuesArrayLength,actualEventValuesArrayLength==expectedEventValuesArrayLength);
  }

  @Test
  public void testShouldExtractFromGoodJsonWithQuotesResponse() throws Exception {
    mockClientResponse(200,"[\"foo\",\"hello\"]");

    String[] actualEventValuesArray = client.getEventValues(SOME_STRING,SOME_STRING);

    int actualEventValuesArrayLength = actualEventValuesArray.length;
    int expectedEventValuesArrayLength = 2;
    assertTrue("Expected Event values array to have "+expectedEventValuesArrayLength+" entries but it had "+actualEventValuesArrayLength,actualEventValuesArrayLength==expectedEventValuesArrayLength);

    String[] expectedEventValuesArray = {"foo","hello"};
    assertArrayEquals("The returned Event values array was not what we expected",expectedEventValuesArray,actualEventValuesArray);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOnNon200Response() throws Exception {
    mockClientResponse(500,null);

    client.getEventValues(SOME_STRING,SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");

    String[] actualEventValuesArray = client.getEventValues(SOME_STRING,SOME_STRING);
    assertNull("Expected a null return value but was not null",actualEventValuesArray);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");

    client.getEventValues(SOME_STRING,SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);

    client.getEventValues(SOME_STRING,SOME_STRING);
  }

  @Test
  public void testWithPrefixShouldSetQueryParam() throws Exception {
    mockClientResponse(200,"[\"foo\"]");
    final String prefixString = "my prefix string";
    new NonStrictExpectations(){{
      // This will override the previoud queryParam expectation...
      webResource.queryParam("prefix",prefixString); result = webResource; times = 1;
      // ...so we must specifically set a result for the queryParam calls here even though it was set for anyString earlier
      webResource.queryParam("event_type",anyString); result = webResource;
      webResource.queryParam("event_key",anyString); result = webResource;
    }};

    client.getEventValues(SOME_STRING,SOME_STRING,prefixString);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEventType() throws Exception {
    mockClientResponse(200, null);
    client.getEventValues(null, SOME_STRING);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEventKey() throws Exception {
    mockClientResponse(200, null);
    client.getEventValues(SOME_STRING, null);
  }

  @Test
  public void testShouldNotSetPrefixQueryParamWithNullPrefix() throws Exception {
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("prefix",anyString); result = webResource; times = 0;
      webResource.queryParam("event_type",anyString); result = webResource;
      webResource.queryParam("event_key",anyString); result = webResource;
      webResource.get(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
      response.getEntity(String.class); result = "[\"foo\"]";
    }};
    client.getEventValues(SOME_STRING, SOME_STRING, null);
  }
}
