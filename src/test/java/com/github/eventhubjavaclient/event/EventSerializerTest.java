package com.github.eventhubjavaclient.event;

import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EventSerializerTest extends EventSerializationTestBase {
  @Test
  public void testEventWith1Property() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_1_PROPERTY, EVENT_1_PROPERTY);
  }

  @Test
  public void testEventWith2Properties() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_2_PROPERTIES, EVENT_2_PROPERTIES);
  }

  @Test
  public void testEventWith0Properties() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_0_PROPERTIES, EVENT_0_PROPERTIES);
  }

  @Test
  public void testEventWithNoDate() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_NO_DATE, EVENT_NO_DATE);
  }

  @Test
  public void testEventWithNoEventType() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_NO_EVENT_TYPE, EVENT_NO_EVENT_TYPE);
  }

  @Test
  public void testEventWithNoUserId() throws Exception {
    testEventIsConvertedToJson(JSON_EVENT_NO_USER_ID, EVENT_NO_USER_ID);
  }

  @Test
  public void testAllJsonEvents() throws Exception {
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    String actualJson = gson.toJson(ALL_EVENTS_SORTED_IN_JSON_ORDER, collectionType);
    String expectedJson = ALL_JSON_EVENTS;
    assertEquals(removeWhiteSpace(expectedJson), actualJson);
  }

  // Util

  private void testEventIsConvertedToJson(final String expectedJson, final Event eventToConvert) {
    String actualJson = gson.toJson(eventToConvert,Event.class);
    // Remove whitespace
    assertEquals(removeWhiteSpace(expectedJson),actualJson);
  }

  private static String removeWhiteSpace(final String s) {
    return s.replaceAll("\\s+", "");
  }
}
