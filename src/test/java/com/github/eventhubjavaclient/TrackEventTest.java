package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class TrackEventTest extends EventHubClientTestBase {

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    Event event = createEvent();
    mockClientResponse(200,SOME_STRING);
    makeClientRequest(event);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    Event event = createEvent();
    mockClientResponse(500,SOME_STRING);
    makeClientRequest(event);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithNoDateInEvent() throws Exception {
    Event event = createEventNoDate();
    mockClientResponse(200,SOME_STRING);
    makeClientRequest(event);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithNullPropertiesInEvent() throws Exception {
    Event event = createEventNullProperties();
    mockClientResponse(200,SOME_STRING);
    makeClientRequest(event);
  }

  private void makeClientRequest(final Event event) throws UnexpectedResponseCodeException {
    client.trackEvent(event);
  }

  private static Event createEvent() {
    final String eventType = "eventType";
    final String externalUserID = "externalUserID";
    DateTime dateTime = new DateTime(2014,12,1,0,0);
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(eventType,externalUserID,dateTime,properties);
  }
  private static Event createEventNullProperties() {
    final String eventType = "eventType";
    final String externalUserID = "externalUserID";
    DateTime dateTime = new DateTime(2014,12,1,0,0);
    return new Event(eventType,externalUserID,dateTime,null);
  }

  private static Event createEventNoDate() {
    final String eventType = "eventType";
    final String externalUserID = "externalUserID";
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(eventType,externalUserID,properties);
  }
}
