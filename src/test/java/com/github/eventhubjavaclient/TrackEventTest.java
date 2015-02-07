package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.api.client.ClientResponse;
import mockit.NonStrictExpectations;
import mockit.integration.junit4.JMockit;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.MediaType;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@RunWith(JMockit.class)
public class TrackEventTest extends EventHubClientTestBase {

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200Response() throws Exception {
    Event event = createEvent();
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(event);
  }

  @Test(expected = UnexpectedResponseCodeException.class)
  public void testShouldThrowUnexpectedResponseCodeExceptionForNon200Response() throws Exception {
    Event event = createEvent();
    mockClientResponse(500,SOME_STRING);
    client.trackEvent(event);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithNoDateInEvent() throws Exception {
    Event event = createEventNoDate();
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(event);
  }

  @Test
  public void testShouldCompleteRequestSuccessfullyFor200ResponseWithNullPropertiesInEvent() throws Exception {
    Event event = createEventNullProperties();
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(event);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForNullEvent() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(null);
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForEventWithNullType() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(createEventNullEventType());
  }

  @Test(expected = IllegalInputException.class)
  public void testShouldThrowIllegalInputExceptionForEventWithNullUserId() throws Exception {
    mockClientResponse(200,SOME_STRING);
    client.trackEvent(createEventNullExternalUserId());
  }
  
  @Test
  public void testShouldAddQueryParamForEventType() throws Exception {
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("event_type", EVENT_TYPE); result = webResource; times = 1;
      webResource.queryParam(anyString, anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};

    client.trackEvent(createEvent());
  }

  @Test
  public void testShouldAddQueryParamForUserId() throws Exception {
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("external_user_id", EXTERNAL_USER_ID); result = webResource; times = 1;
      webResource.queryParam(anyString, anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.trackEvent(createEvent());
  }

  @Test
  public void testShouldAddQueryParamForDate() throws Exception {
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("date", "20141201"); result = webResource; times = 1;
      webResource.queryParam(anyString, anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.trackEvent(createEvent());
  }

  @Test
  public void testShouldAddQueryParamsForAllEventProperties() throws Exception {
    new NonStrictExpectations() {{
      webResource.path(anyString); result = webResource;
      webResource.queryParam("property1", "value1"); result = webResource; times = 1;
      webResource.queryParam(anyString, anyString); result = webResource;
      webResource.header(anyString, anyString); result = builder;
      builder.accept(withAny(MediaType.APPLICATION_JSON_TYPE)); result = builder;
      builder.post(ClientResponse.class); result = response;
      response.getStatus(); result = 200;
    }};
    client.trackEvent(createEvent());
  }

  // Util

  private static final String EVENT_TYPE = "event type";
  private static final String EXTERNAL_USER_ID = "external User ID";
  private static final DateTime DATE_TIME = new DateTime(2014,12,1,0,0);

  private static Event createEvent() {
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(EVENT_TYPE,EXTERNAL_USER_ID, DATE_TIME,properties);
  }

  private static Event createEventNullProperties() {
    return new Event(EVENT_TYPE, EXTERNAL_USER_ID,DATE_TIME,null);
  }

  private static Event createEventNoDate() {
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(EVENT_TYPE,EXTERNAL_USER_ID,properties);
  }

  private static Event createEventNullEventType() {
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(null,EXTERNAL_USER_ID,DATE_TIME,properties);
  }

  private static Event createEventNullExternalUserId() {
    DateTime dateTime = new DateTime(2014,12,1,0,0);
    Map<String,String> properties = new HashMap<String, String>();
    properties.put("property1","value1");
    return new Event(EVENT_TYPE,null,dateTime,properties);
  }
}
