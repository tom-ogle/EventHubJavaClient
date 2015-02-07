package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertArrayEquals;

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

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullStartDate() throws Exception {
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    client.retrieveEventFunnelCounts(null,new DateTime(),new String[]{"one"},7);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEndDate() throws Exception {
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    client.retrieveEventFunnelCounts(new DateTime(),null,new String[]{"one"},7);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForEmptyFunnelSteps() throws Exception {
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    client.retrieveEventFunnelCounts(new DateTime(),new DateTime(),new String[]{},7);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullFunnelSteps() throws Exception {
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    client.retrieveEventFunnelCounts(new DateTime(),new DateTime(),null,7);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNonPositiveDayToComplete() throws Exception {
    mockClientResponse(200,"[\n  4,\n  3,\n  2\n]\n");
    client.retrieveEventFunnelCounts(new DateTime(),new DateTime(),new String[]{"one"},-1);
  }

  @Test
  public void testShouldCallPostWithCorrectBody() throws Exception {
    DateTime startDate = new DateTime(2013, 12, 25, 12, 0, 0, 0);
    DateTime endDate = new DateTime(2014, 12, 25, 12, 0, 0, 0);
    String[] funnelSteps = new String[]{"one"};
    int daysToCompleteFunnel = 7;
    final String expectedBody = "start_date=20131225&end_date=20141225&funnel_steps[]=one&num_days_to_complete_funnel=7";
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.post(ClientResponse.class, expectedBody); result = response; times = 1;
      response.getStatus(); result = 200;
      response.getEntity(String.class); result = "[\n  4,\n  3,\n  2\n]\n";
    }};
    client.retrieveEventFunnelCounts(startDate, endDate, funnelSteps, daysToCompleteFunnel);
  }

  // Util

  private int[] makeClientRequest() throws Exception {
    return client.retrieveEventFunnelCounts(new DateTime(),new DateTime(),new String[]{"one"},7);
  }
}
