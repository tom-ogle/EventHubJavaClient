package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests for the getUserKeys method.
 */
@RunWith(JMockit.class)
public class GetUserKeysTest extends EventHubClientTestBase {

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    mockClientResponse(200,"[\n  \"foo\",\n  \"hello\"\n]\n");

    String[] actualUserKeysArray = client.getUserKeys();

    int actualUserKeysArrayLength = actualUserKeysArray.length;
    int expectedUserKeysArrayLength = 2;
    assertTrue("Expected user keys array to have "+expectedUserKeysArrayLength+" entries but it had "+actualUserKeysArrayLength,actualUserKeysArrayLength==expectedUserKeysArrayLength);

    String[] expectedUserKeysArray = {"foo","hello"};
    assertArrayEquals("The returned user keys array was not what we expected",expectedUserKeysArray,actualUserKeysArray);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    mockClientResponse(200,"[]");

    String[] actualUserKeysArray = client.getUserKeys();

    int actualUserKeysArrayLength = actualUserKeysArray.length;
    int expectedUserKeysArrayLength = 0;
    assertTrue("Expected user keys array to have "+expectedUserKeysArrayLength+" entries but it had "+actualUserKeysArrayLength,actualUserKeysArrayLength==expectedUserKeysArrayLength);
  }

  @Test
  public void testShouldExtractFromGoodJsonWithQuotesResponse() throws Exception {
    mockClientResponse(200,"[\"foo\",\"hello\"]");

    String[] actualUserKeysArray = client.getUserKeys();

    int actualUserKeysArrayLength = actualUserKeysArray.length;
    int expectedUserKeysArrayLength = 2;
    assertTrue("Expected user keys array to have "+expectedUserKeysArrayLength+" entries but it had "+actualUserKeysArrayLength,actualUserKeysArrayLength==expectedUserKeysArrayLength);

    String[] expectedUserKeysArray = {"foo","hello"};
    assertArrayEquals("The returned user keys array was not what we expected",expectedUserKeysArray,actualUserKeysArray);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOn500Response() throws Exception {
    mockClientResponse(500,null);

    client.getUserKeys();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");

    String[] actualUserKeysArray = client.getUserKeys();
    assertNull("Expected a null return value but was not null",actualUserKeysArray);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");

    client.getUserKeys();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);

    client.getUserKeys();
  }
}
