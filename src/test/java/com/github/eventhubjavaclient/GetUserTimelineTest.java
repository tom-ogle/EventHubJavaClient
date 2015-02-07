package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.event.EventSerializationTestBase;
import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.google.gson.JsonSyntaxException;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForNullEntity() throws Exception {
    mockClientResponse(200, null);
    client.getUserTimeline(USER_NAME,0,10);
  }

  @Test(expected = BadlyFormedResponseBodyException.class)
  public void testShouldThrowBadlyFormedResponseBodyExceptionForEmptyStringEntity() throws Exception {
    mockClientResponse(200, null);
    client.getUserTimeline(USER_NAME,0,10);
  }

  @Test(expected = JsonSyntaxException.class)
  public void testShouldThrowJsonSyntaxExceptionExceptionForBadJsonEntity() throws Exception {
    mockClientResponse(200, "{");
    client.getUserTimeline(USER_NAME,0,10);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullUserName() throws Exception {
    mockClientResponse(200,EventSerializationTestBase.ALL_JSON_EVENTS);
    client.getUserTimeline(null,0,10);
  }
}
