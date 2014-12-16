package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import mockit.integration.junit4.JMockit;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(JMockit.class)
public class BatchTrackEventTest extends EventHubClientTestBase {

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    List<Event> events = produceEventList();
    mockClientResponse(200,SOME_STRING);
    client.batchTrackEvent(events);
  }

  private static List<Event> produceEventList() {
    List<Event> events = new ArrayList<Event>();

    return events;
  }
}
