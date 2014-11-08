package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the getUserValues method.
 */
@RunWith(JMockit.class)
public class GetUserValuesTest extends EventHubClientTestBase {

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    mockClientResponse(200,"[\n  \"foo\",\n  \"hello\"\n]\n");

    String[] actualUserValuesArray = client.getUserValues(SOME_STRING);

    int actualUserValuesArrayLength = actualUserValuesArray.length;
    int expectedUserValuesArrayLength = 2;
    assertTrue("Expected user values array to have "+expectedUserValuesArrayLength+" entries but it had "+actualUserValuesArrayLength,actualUserValuesArrayLength==expectedUserValuesArrayLength);

    String[] expectedUserValuesArray = {"foo","hello"};
    assertArrayEquals("The returned user values array was not what we expected",expectedUserValuesArray,actualUserValuesArray);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    mockClientResponse(200,"[]");

    String[] actualUserValuesArray = client.getUserValues(SOME_STRING);

    int actualUserValuesArrayLength = actualUserValuesArray.length;
    int expectedUserValuesArrayLength = 0;
    assertTrue("Expected user values array to have "+expectedUserValuesArrayLength+" entries but it had "+actualUserValuesArrayLength,actualUserValuesArrayLength==expectedUserValuesArrayLength);
  }

  @Test
  public void testShouldExtractFromGoodJsonWithQuotesResponse() throws Exception {
    mockClientResponse(200,"[\"foo\",\"hello\"]");

    String[] actualUserValuesArray = client.getUserValues(SOME_STRING);

    int actualUserValuesArrayLength = actualUserValuesArray.length;
    int expectedUserValuesArrayLength = 2;
    assertTrue("Expected user values array to have "+expectedUserValuesArrayLength+" entries but it had "+actualUserValuesArrayLength,actualUserValuesArrayLength==expectedUserValuesArrayLength);

    String[] expectedUserValuesArray = {"foo","hello"};
    assertArrayEquals("The returned user values array was not what we expected",expectedUserValuesArray,actualUserValuesArray);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOn500Response() throws Exception {
    mockClientResponse(500,null);

    client.getUserValues(SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");

    String[] actualUserValuesArray = client.getUserValues(SOME_STRING);
    assertNull("Expected a null return value but was not null",actualUserValuesArray);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");

    client.getUserValues(SOME_STRING);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);

    client.getUserValues(SOME_STRING);
  }

  @Test
  public void testWithPrefixShouldSetQueryParam() throws Exception {
    mockClientResponse(200,"[\"foo\"]");
    final String prefixString = "my prefix string";
    new NonStrictExpectations(){{
      // This will override the previoud queryParam expectation...
      webResource.queryParam("prefix",prefixString); result = webResource; times = 1;
      // ...so we must specifically set a result for the user_key call to queryParam here even though it was set for anyString earlier
      webResource.queryParam("user_key", anyString); result = webResource;
    }};

    client.getUserValues(SOME_STRING,prefixString);
  }

  @Test(expected = NullPointerException.class)
  public void testShouldThrowNPEForNullUserKeyInput() throws Exception {
    mockClientResponse(200,"[\"foo\"]");
    client.getUserValues(null);
  }
}
