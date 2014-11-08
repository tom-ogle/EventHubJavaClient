package com.github.eventhubjavaclient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.Before;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public class EventHubClientTestBase {

  protected static final String SOME_STRING = "some string"; // use this where the value doesn't matter

  protected EventHubClient client;
  @Mocked WebResource webResource;
  @Mocked WebResource.Builder builder;
  @Mocked ClientResponse response;

  protected void mockClientResponse(final int responseReturnCode, final String body) {
    new NonStrictExpectations(){{
      webResource.path(anyString); result = webResource;
      webResource.queryParam(anyString,anyString); result = webResource;
      webResource.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.get(ClientResponse.class); result = response;
      webResource.get(ClientResponse.class); result = response;
      response.getStatus(); result = responseReturnCode;
      response.getEntity(String.class); result = body;
    }};
  }

  @Before
  public void setUp() {
    client = EventHubClient.createDefaultClient("http://acceptableurl");
  }
}
