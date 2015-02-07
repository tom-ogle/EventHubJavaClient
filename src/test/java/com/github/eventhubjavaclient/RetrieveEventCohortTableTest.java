package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

@RunWith(JMockit.class)
public class RetrieveEventCohortTableTest extends EventHubClientTestBase {

  private DateTime startDate;
  private DateTime endDate;
  private String rowEventType;
  private String columnEventType;
  private int noOfDaysPerRow;
  private int noOfDaysPerColumn;
  private Map<String, String> rowFilters;
  private Map<String, String> columnFilters;

  @Before
  public void setUpTest() {
    rowFilters = new HashMap<String, String>();
    columnFilters = new HashMap<String, String>();

    rowFilters.put("rf1", "rfv1");
    columnFilters.put("cf1", "cfv1");

    startDate = new DateTime(2013, 12, 25, 12, 0, 0, 0);
    endDate = new DateTime(2014, 12, 25, 12, 0, 0, 0);
    rowEventType = "rowEventType";
    columnEventType = "columnEventType";
    noOfDaysPerRow = 1;
    noOfDaysPerColumn = 1;
  }

  @Test
  public void testShouldCallPostWithCorrectBody() throws Exception {
    final String expectedBody = "start_date=20131225&end_date=20141225&row_event_type=rowEventType&column_event_type=columnEventType&num_days_per_row=1&num_columns=1&refk[]=rf1&refv[]=rfv1&cefk[]=cf1&cefv[]=cfv1";

    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.post(ClientResponse.class, expectedBody); result = response; times = 1;
      response.getStatus(); result = 200;
      response.getEntity(String.class); result = "[[\n  4,\n  3,\n  2\n]]\n";
    }};
    makeClientRequest();
  }

  @Test
  public void testShouldExtractFromGoodJsonResponse() throws Exception {
    int[][] expectedFunnelEventCounts = new int[][] {
        {1, 2, 5},
        {7, 675},
        {1},
        {203, 9876}
    };
    mockClientResponse(200,"[ [1, 2, 5], [7, 675], [1], [203, 9876]]\n");
    int[][] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test
  public void testShouldExtractFromGoodJsonResponseWithNewlines() throws Exception {
    int[][] expectedFunnelEventCounts = new int[][] {
        {1, 2, 5},
        {7, 675},
        {1},
        {203, 9876}
    };
    mockClientResponse(200,"[\n [1,\n 2,\n 5],\n [7,\n 675\n],\n [1],\n [203,\n 9876\n]\n]\n");
    int[][] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test
  public void testShouldReturnEmptyArrayForEmptyJsonArrayResponse() throws Exception {
    int[][] expectedFunnelEventCounts = new int[][]{};
    mockClientResponse(200,"[]");
    int[][] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test
  public void testShouldReturnNestedEmptyArrayForNestedEmptyJsonArrayResponse() throws Exception {
    int[][] expectedFunnelEventCounts = new int[][]{new int[]{}};
    mockClientResponse(200,"[[]]");
    int[][] actualEventFunnelCounts = makeClientRequest();
    assertArrayEquals("Expected and actual funnel event counts did not match", expectedFunnelEventCounts, actualEventFunnelCounts);
  }

  @Test
  public void testShouldReturnNestedEmptyArrayForNestedEmptyJsonArrayResponse2() throws Exception {
    int[][] expectedFunnelEventCounts = new int[][]{new int[]{}, new int[]{}};
    mockClientResponse(200,"[[], []]");
    int[][] actualEventFunnelCounts = makeClientRequest();
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
    mockClientResponse(200,"[[]]");
    startDate = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEndDate() throws Exception {
    mockClientResponse(200,"[[]]");
    endDate = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullRowEventType() throws Exception {
    mockClientResponse(200,"[[]]");
    rowEventType = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullColumnEventType() throws Exception {
    mockClientResponse(200,"[[]]");
    columnEventType = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullRowFilters() throws Exception {
    mockClientResponse(200,"[[]]");
    rowFilters = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullColumnFilters() throws Exception {
    mockClientResponse(200,"[[]]");
    columnFilters = null;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForZeroNumberOfDaysPerRow() throws Exception {
    mockClientResponse(200,"[[]]");
    noOfDaysPerRow = 0;
    makeClientRequest();
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForZeroNumberOfDaysPerColumn() throws Exception {
    mockClientResponse(200,"[[]]");
    noOfDaysPerColumn = 0;
    makeClientRequest();
  }


  private int[][] makeClientRequest() throws Exception {
    return client.retrieveEventCohortTable(startDate, endDate, rowEventType, columnEventType, noOfDaysPerRow, noOfDaysPerColumn,
        rowFilters, columnFilters);
  }
}
