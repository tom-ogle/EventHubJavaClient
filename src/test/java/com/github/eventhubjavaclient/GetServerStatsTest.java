package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class)
public class GetServerStatsTest extends EventHubClientTestBase {

  private static final String EXPECTED_SERVER_STATS = "server stats";

  @Test
  public void testShouldCompleteRequestSuccessfullyReturningBodyEntityFor200Response() throws Exception {
    mockClientResponse(200, EXPECTED_SERVER_STATS);
    String actualServerStats = client.getServerStats();
    assertEquals(format("Expected server stats to be '%s' but it was actually '%s'", EXPECTED_SERVER_STATS, actualServerStats),
        EXPECTED_SERVER_STATS, actualServerStats);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    mockClientResponse(500, EXPECTED_SERVER_STATS);
    client.getServerStats();
  }
}