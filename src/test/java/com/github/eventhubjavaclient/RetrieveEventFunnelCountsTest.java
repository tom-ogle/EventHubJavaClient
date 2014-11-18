package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

/**
 *
 */
@RunWith(JMockit.class)
public class RetrieveEventFunnelCountsTest extends EventHubClientTestBase {

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    int[] expectedFunnelEventCounts = {4,3,2};
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    int[] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    int[] expectedFunnelEventCounts = new int[]{};
        mockClientResponse(200,"[]");
    int[] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionOn500Response() throws Exception {
    mockClientResponse(500,null);
    makeClientRequest();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody() throws Exception {
    mockClientResponse(200,"{badlyformedresponsebody");
    makeClientRequest();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForBadlyFormedResponseBody2() throws Exception {
    mockClientResponse(200,"[badlyformedresponsebody");
    makeClientRequest();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyReturnBody() throws Exception {
    mockClientResponse(200,"");
    makeClientRequest();
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullReturnBody() throws Exception {
    mockClientResponse(200,null);
    makeClientRequest();
  }

  // Util

  private int[] makeClientRequest() throws Exception {
    return client.retrieveEventFunnelCounts(new DateTime(),new DateTime(),new String[]{"one"},7);
  }
}
