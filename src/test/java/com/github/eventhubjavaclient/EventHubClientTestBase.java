package com.github.eventhubjavaclient;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.PartialRequestBuilder;
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

  protected void mockClientResponse(final int responseReturnCode, final String responseEntity) {
    new NonStrictExpectations(){{
      webResource.path(anyString); result = webResource;
      webResource.header(anyString,any); result = builder;
      webResource.queryParam(anyString,anyString); result = webResource;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      webResource.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      // Mock Get request
      builder.get(ClientResponse.class); result = response;
      webResource.get(ClientResponse.class); result = response;
      // Mock Post request
      builder.post(ClientResponse.class,anyString); result = response;
      builder.post(ClientResponse.class); result = response;
      webResource.post(ClientResponse.class); result = response;
      // Mock the response
      response.getStatus(); result = responseReturnCode;
      response.getEntity(String.class); result = responseEntity;
    }};
  }

  @Before
  public void setUp() {
    client = EventHubClient.createDefaultClient("http://acceptableurl");
  }
}
