package com.github.eventhubjavaclient.event;

import com.google.gson.Gson;
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
public class EventDeserializerTest {

  private Gson gson;

  private static final Event EVENT_1_PROPERTY = constructExpectedEvent1Property();
  private static final String JSON_EVENT_1_PROPERTY =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"submission\",\n"
          + "    \"exercise\": \"homepage_1\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEvent1Property() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("exercise","homepage_1");
    return new Event("submission","generated_id_123","20141122",expectedProperties);
  }

  private static final Event EVENT_2_PROPERTIES = constructExpectedEvent2Properties();
  private static final String JSON_EVENT_2_PROPERTIES =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"signup\",\n"
          + "    \"experiment\": \"signup_v1\",\n"
          + "    \"external_user_id\": \"generated_id_123\",\n"
          + "    \"treatment\": \"control\"\n"
          + "  }";

  private static Event constructExpectedEvent2Properties() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("experiment","signup_v1");
    expectedProperties.put("treatment","control");
    return new Event("signup","generated_id_123","20141122",expectedProperties);
  }

  private static final Event EVENT_0_PROPERTIES = constructExpectedEvent0Properties();
  private static final String JSON_EVENT_0_PROPERTIES =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEvent0Properties() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent","generated_id_123","20141122",expectedProperties);
  }

  private static final Event EVENT_NO_DATE = constructExpectedEventNoDate();
  private static final String JSON_EVENT_NO_DATE =
      "  {\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEventNoDate() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent","generated_id_123",null,expectedProperties);
  }

  private static final Event EVENT_NO_EVENT_TYPE = constructExpectedEventNoEventType();
  private static final String JSON_EVENT_NO_EVENT_TYPE =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEventNoEventType() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event(null,"generated_id_123","20141122",expectedProperties);
  }

  private static final Event EVENT_NO_USER_ID = constructExpectedEventNoUserId();
  private static final String JSON_EVENT_NO_USER_ID =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"email_sent\"\n"
          + "  }";

  private static Event constructExpectedEventNoUserId() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent",null,"20141122",expectedProperties);
  }

  private static Collection<Event> ALL_EVENTS;

  private static final String ALL_JSON_EVENTS =
      "[\n"
          + JSON_EVENT_1_PROPERTY + ",\n" + JSON_EVENT_2_PROPERTIES + ",\n" + JSON_EVENT_0_PROPERTIES + ",\n"
          + JSON_EVENT_NO_DATE + ",\n" + JSON_EVENT_NO_EVENT_TYPE + ",\n" + JSON_EVENT_NO_USER_ID
          + "]\n";

  private static Map<String, Event> jsonToEventMap;
  private static Map<Event,String > eventToJsonMap;

  static {
    initializeEventMaps();
    ALL_EVENTS = jsonToEventMap.values();
  }

  protected static void initializeEventMaps() {
    initializeJsonToEventMap();
    initializeEventToJsonMap();
  }

  private static void initializeJsonToEventMap() {
    jsonToEventMap = new HashMap<String, Event>();
    jsonToEventMap.put(JSON_EVENT_1_PROPERTY, EVENT_1_PROPERTY);
    jsonToEventMap.put(JSON_EVENT_2_PROPERTIES, EVENT_2_PROPERTIES);
    jsonToEventMap.put(JSON_EVENT_0_PROPERTIES, EVENT_0_PROPERTIES);
    jsonToEventMap.put(JSON_EVENT_NO_DATE, EVENT_NO_DATE);
    jsonToEventMap.put(JSON_EVENT_NO_EVENT_TYPE, EVENT_NO_EVENT_TYPE);
    jsonToEventMap.put(JSON_EVENT_NO_USER_ID, EVENT_NO_USER_ID);
  }

  private static void initializeEventToJsonMap() {
    eventToJsonMap = new HashMap<Event, String>();
    eventToJsonMap.put(EVENT_1_PROPERTY, JSON_EVENT_1_PROPERTY);
    eventToJsonMap.put(EVENT_2_PROPERTIES, JSON_EVENT_2_PROPERTIES);
    eventToJsonMap.put(EVENT_0_PROPERTIES, JSON_EVENT_0_PROPERTIES);
    eventToJsonMap.put(EVENT_NO_DATE, JSON_EVENT_NO_DATE);
    eventToJsonMap.put(EVENT_NO_EVENT_TYPE, JSON_EVENT_NO_EVENT_TYPE);
    eventToJsonMap.put(EVENT_NO_USER_ID, JSON_EVENT_NO_USER_ID);
  }

  private static final Comparator<Event> eventComparator = new Comparator<Event>() {
    @Override public int compare(final Event event, final Event event2) {
      return event.hashCode() - event2.hashCode();
    }
  };

  @Before
  public void setUp() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
    gson = gsonBuilder.create();
  }

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
