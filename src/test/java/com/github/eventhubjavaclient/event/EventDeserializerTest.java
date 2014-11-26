package com.github.eventhubjavaclient.event;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EventDeserializerTest extends EventSerializationTestBase {

  @Test
  public void testEventWith1Property() throws Exception {
    Event expectedEvent = EVENT_1_PROPERTY;
    Event actualEvent = gson.fromJson(JSON_EVENT_1_PROPERTY,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testEventWith2Properties() throws Exception {
    Event expectedEvent = EVENT_2_PROPERTIES;
    Event actualEvent = gson.fromJson(JSON_EVENT_2_PROPERTIES,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testEventWith0Properties() throws Exception {
    Event expectedEvent = EVENT_0_PROPERTIES;
    Event actualEvent = gson.fromJson(JSON_EVENT_0_PROPERTIES,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testEventWithNoDate() throws Exception {
    Event expectedEvent = EVENT_NO_DATE;
    Event actualEvent = gson.fromJson(JSON_EVENT_NO_DATE,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testEventWithNoEventType() throws Exception {
    Event expectedEvent = EVENT_NO_EVENT_TYPE;
    Event actualEvent = gson.fromJson(JSON_EVENT_NO_EVENT_TYPE,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testEventWithNoUserId() throws Exception {
    Event expectedEvent = EVENT_NO_USER_ID;
    Event actualEvent = gson.fromJson(JSON_EVENT_NO_USER_ID,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  @Test
  public void testAllJsonEvents() throws Exception {
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    Collection<Event> actualAllUserEvents = gson.fromJson(ALL_JSON_EVENTS,collectionType);
    List<Event> actualAllEventsSorted = new ArrayList<Event>(actualAllUserEvents);
    Collections.sort(actualAllEventsSorted,eventComparator);
    List<Event> expectedAllEventsSorted = new ArrayList<Event>(ALL_EVENTS);
    Collections.sort(expectedAllEventsSorted,eventComparator);
    assertEquals(expectedAllEventsSorted,actualAllEventsSorted);
  }

}
