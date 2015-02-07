package com.github.eventhubjavaclient;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class ObjectCreationTest {

  private static final String GOOD_URL = "http://someurl";
  private static final String BAD_URL = "some bad url";

  private static final Integer CONNECTION_TIMEOUT = 60000;
  private static final Integer READ_TIMEOUT = 60000;

  @Test
  public void testCreationMethodsProduceClient() throws Exception {

    EventHubClient producedClient  = EventHubClient.createDefaultClient(GOOD_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
    assertNotNull("createDefaultClient returned a null client",producedClient);

    ClientConfig config = new DefaultClientConfig();
    producedClient = EventHubClient.createCustomClient(GOOD_URL, CONNECTION_TIMEOUT, READ_TIMEOUT, config);
    assertNotNull("createCustomClient returned a null client", producedClient);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDefaultClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    EventHubClient.createDefaultClient(BAD_URL, CONNECTION_TIMEOUT, READ_TIMEOUT);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCustomClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    ClientConfig config = new DefaultClientConfig();
    EventHubClient.createCustomClient(BAD_URL, CONNECTION_TIMEOUT, READ_TIMEOUT, config);
  }
}
