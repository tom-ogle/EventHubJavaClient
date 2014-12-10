package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.event.EventSerializationTestBase;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.*;

@RunWith(JMockit.class)
public class GetUserTimelineTest extends EventHubClientTestBase {

  private static final String USER_NAME = "username";

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    mockClientResponse(200, EventSerializationTestBase.ALL_JSON_EVENTS);
    client.getUserTimeline(USER_NAME,0,10);
  }

  @Test
  public void testShouldExtractCorrectEntitiesFor200Response() throws Exception {
    mockClientResponse(200, EventSerializationTestBase.ALL_JSON_EVENTS);
    Collection<Event> actualEventsCollection = client.getUserTimeline(USER_NAME,0,10);

    List<Event> actualEvents = new ArrayList<Event>(actualEventsCollection);
    List<Event> expectedEvents = EventSerializationTestBase.ALL_EVENTS_SORTED_IN_JSON_ORDER;
    EventSerializationTestBase.assertThatListsOfEventsHaveSameValues(expectedEvents,actualEvents);

  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    mockClientResponse(500, EventSerializationTestBase.ALL_JSON_EVENTS);
    client.getUserTimeline(USER_NAME,0,10);
  }

  // TODO: Tests for bad json returned

  // Util

  private static Collection<Event> createUserTimeline() {
    Collection<Event> timeline = new ArrayList<Event>();
    final String eventType = "some event type";
    final String externalUserId = "externalUserId";
    DateTime dateTime = new DateTime(2014,12,11,0,0);
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("property1", "value1");
    timeline.add(new Event(eventType,externalUserId,dateTime,properties));
    return timeline;
  }

}
