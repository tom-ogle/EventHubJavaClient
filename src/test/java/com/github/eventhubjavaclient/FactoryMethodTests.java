package com.github.eventhubjavaclient;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 *
 */
public class FactoryMethodTests {

  private static final String GOOD_URL = "http://someurl";
  private static final String BAD_URL = "some bad url";

  @Test
  public void testFactoryMethodsProduceClient() throws Exception {

    EventHubClient factoryProducedClient  = EventHubClient.createDefaultClient(GOOD_URL);
    assertNotNull("Factory method createDefaultClient returned a null client",factoryProducedClient);

    ClientConfig config = new DefaultClientConfig();
    factoryProducedClient = EventHubClient.createCustomClient(GOOD_URL,config);
    assertNotNull("Factory method createCustomClient returned a null client", factoryProducedClient);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testcreateDefaultClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    EventHubClient.createDefaultClient(BAD_URL);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testcreateCustomClientShouldThrowIllegalArgumentExceptionForBadUrl() throws Exception {
    ClientConfig config = new DefaultClientConfig();
    EventHubClient.createCustomClient(BAD_URL, config);
  }
}
