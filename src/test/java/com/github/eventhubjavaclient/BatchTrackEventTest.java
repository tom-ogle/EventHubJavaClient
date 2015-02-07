package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(JMockit.class)
public class BatchTrackEventTest extends EventHubClientTestBase {

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    List<Event> events = produceEventList();
    mockClientResponse(200,SOME_STRING);
    client.batchTrackEvents(events);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    List<Event> events = produceEventList();
    mockClientResponse(500,SOME_STRING);
    client.batchTrackEvents(events);
  }

  @Test
  public void testShouldCallPostWithCorrectJsonBody() throws Exception {
    List<Event> events = produceEventList();
    final String expectedBody = "events=[{\"event_type\":\"click\",\"external_user_id\":\"userid\"}]";
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.post(ClientResponse.class, expectedBody); result = response; times = 1;
      response.getStatus(); result = 200;
    }};
    client.batchTrackEvents(events);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEventList() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.batchTrackEvents(null);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForEmptyEventList() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.batchTrackEvents(new ArrayList<Event>());
  }

  private static List<Event> produceEventList() {
    List<Event> events = new ArrayList<Event>();
    Event event = new Event("click","userid",new HashMap<String, String>());
    events.add(event);
    return events;
  }
}
