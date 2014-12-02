package com.github.eventhubjavaclient.event;

import com.google.gson.reflect.TypeToken;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.*;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static com.github.eventhubjavaclient.EventHubClientUtils.EVENT_HUB_DATE_FORMATTER;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class EventDeserializerTest extends EventSerializationTestBase {

  @Test
  public void testEventWith1Property() throws Exception {
    testJsonIsConvertedToEvent(EVENT_1_PROPERTY, JSON_EVENT_1_PROPERTY);
  }

  @Test
  public void testEventWith2Properties() throws Exception {
    testJsonIsConvertedToEvent(EVENT_2_PROPERTIES, JSON_EVENT_2_PROPERTIES);
  }

  @Test
  public void testEventWith0Properties() throws Exception {
    testJsonIsConvertedToEvent(EVENT_0_PROPERTIES, JSON_EVENT_0_PROPERTIES);
  }

  @Test
  public void testEventWithNoDate() throws Exception {
    testJsonIsConvertedToEvent(EVENT_NO_DATE, JSON_EVENT_NO_DATE);
  }

  @Test
  public void testEventWithNoEventType() throws Exception {
    testJsonIsConvertedToEvent(EVENT_NO_EVENT_TYPE, JSON_EVENT_NO_EVENT_TYPE);
  }

  @Test
  public void testEventWithNoUserId() throws Exception {
    testJsonIsConvertedToEvent(EVENT_NO_USER_ID, JSON_EVENT_NO_USER_ID);
  }

  @Test
  public void testAllJsonEvents() throws Exception {
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    Collection<Event> actualAllUserEvents = gson.fromJson(ALL_JSON_EVENTS,collectionType);
    List<Event> actualAllEventsSorted = new ArrayList<Event>(actualAllUserEvents);
    Collections.sort(actualAllEventsSorted,eventComparator);
    List<Event> expectedAllEventsSorted = new ArrayList<Event>(ALL_EVENTS_SORTED_IN_JSON_ORDER);
    Collections.sort(expectedAllEventsSorted,eventComparator);
    assertEquals(expectedAllEventsSorted,actualAllEventsSorted);
  }

  // Util

  private void testJsonIsConvertedToEvent(final Event expectedEvent, final String jsonToConvert) {
    Event actualEvent = gson.fromJson(jsonToConvert,Event.class);
    assertThatEventsHaveSameValues(expectedEvent, actualEvent);
  }

  private void assertThatEventsHaveSameValues(final Event expectedEvent, final Event actualEvent) {
    final String expectedEventType = expectedEvent.getEventType();
    final String actualEventType = actualEvent.getEventType();
    assertEquals(format("Expected event type was %s but actual was %s",expectedEventType,actualEventType),expectedEventType,actualEventType);

    final String expectedExternalUserId = expectedEvent.getExternalUserId();
    final String actualExternalUserId = actualEvent.getExternalUserId();
    assertEquals(format("Expected external user ID was %s but actual was %s",expectedExternalUserId,actualExternalUserId)
        ,expectedExternalUserId,actualExternalUserId);

    final Set<Map.Entry<String, String>> expectedEventPropertyEntrySet = expectedEvent.getPropertyEntrySet();
    final Set<Map.Entry<String, String>> actualEventPropertyEntrySet = actualEvent.getPropertyEntrySet();
    assertEquals("Expected and actual Event properties were not equal",expectedEventPropertyEntrySet,actualEventPropertyEntrySet);

    final DateTime expectedDateTime = expectedEvent.getDate();
    final DateTime actualDateTime = actualEvent.getDate();
    if(expectedDateTime==null || actualDateTime==null) {
      assertNull("actualDateTime was null but expectedDateTime wasn't",expectedDateTime);
      assertNull("expectedDateTime was null but actualDateTime wasn't",actualDateTime);
      return;
    }

    final String expectedDateTimeString = expectedDateTime.toString(EVENT_HUB_DATE_FORMATTER);
    final String actualDateTimeString = actualDateTime.toString(EVENT_HUB_DATE_FORMATTER);
    assertEquals(expectedDateTimeString,actualDateTimeString);
  }

}
