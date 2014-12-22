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

  @Test
  public void testCreationMethodsProduceClient() throws Exception {

    EventHubClient producedClient  = EventHubClient.createDefaultClient(GOOD_URL);
    assertNotNull("createDefaultClient returned a null client",producedClient);

    ClientConfig config = new DefaultClientConfig();
    producedClient = EventHubClient.createCustomClient(GOOD_URL,config);
    assertNotNull("createCustomClient returned a null client", producedClient);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDefaultClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    EventHubClient.createDefaultClient(BAD_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateCustomClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    ClientConfig config = new DefaultClientConfig();
    EventHubClient.createCustomClient(BAD_URL, config);
  }
}
