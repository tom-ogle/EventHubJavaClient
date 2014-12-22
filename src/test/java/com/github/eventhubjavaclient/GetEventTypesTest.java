package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 *
 */
@RunWith(JMockit.class)
public class GetEventTypesTest extends EventHubClientTestBase {

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    mockClientResponse(200, "[\n  \"pageview\",\n  \"signup\",\n  \"start_track\",\n  \"submission\"\n]\n");

    final String[] expectedEventTypesArray = {"pageview","signup", "start_track", "submission"};
    String[] actualEventTypesArray = client.getEventTypes();
    assertArrayEquals("The returned event types array was not what we expected", expectedEventTypesArray,actualEventTypesArray);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    mockClientResponse(200,"[]");

    String[] actualEventTypesArray = client.getEventTypes();

    int actualEventTypesArrayLength = actualEventTypesArray.length;
    int expectedEventTypesArrayLength = 0;
    assertTrue("Expected event types array to have "+expectedEventTypesArrayLength+" entries but it had "+actualEventTypesArrayLength,actualEventTypesArrayLength==expectedEventTypesArrayLength);
  }

  @Test
  public void testShouldExtractFromGoodJsonWithQuotesResponse() throws Exception {
    mockClientResponse(200, "[\"pageview\",\"signup\",\"start_track\",\"submission\"]");

    String[] actualEventTypesArray = client.getEventTypes();
    final String[] expectedEventTypesArray = {"pageview","signup", "start_track", "submission"};
    assertArrayEquals("The returned event types array was not what we expected", expectedEventTypesArray,actualEventTypesArray);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOn500Response() throws Exception {
    mockClientResponse(500,null);

    client.getEventTypes();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");

    String[] actualEventTypesArray = client.getEventTypes();
    assertNull("Expected a null return value but was not null",actualEventTypesArray);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");

    client.getEventTypes();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);

    client.getEventTypes();
  }
}
