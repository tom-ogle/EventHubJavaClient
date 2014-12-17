package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
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

  private static List<Event> produceEventList() {
    List<Event> events = new ArrayList<Event>();
    Event event = new Event("click","userid",new HashMap<String, String>());
    events.add(event);
    return events;
  }
}
