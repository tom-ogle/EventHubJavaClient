package com.github.eventhubjavaclient.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class EventSerializationTestBase {

  @Before
  public void setUp() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
    gson = gsonBuilder.create();
  }

  protected Gson gson;

  protected static final Event EVENT_1_PROPERTY = constructExpectedEvent1Property();
  protected static final String JSON_EVENT_1_PROPERTY =
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

  protected static final Event EVENT_2_PROPERTIES = constructExpectedEvent2Properties();
  protected static final String JSON_EVENT_2_PROPERTIES =
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

  protected static final Event EVENT_0_PROPERTIES = constructExpectedEvent0Properties();
  protected static final String JSON_EVENT_0_PROPERTIES =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEvent0Properties() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent","generated_id_123","20141122",expectedProperties);
  }

  protected static final Event EVENT_NO_DATE = constructExpectedEventNoDate();
  protected static final String JSON_EVENT_NO_DATE =
      "  {\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEventNoDate() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent","generated_id_123",null,expectedProperties);
  }

  protected static final Event EVENT_NO_EVENT_TYPE = constructExpectedEventNoEventType();
  protected static final String JSON_EVENT_NO_EVENT_TYPE =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEventNoEventType() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event(null,"generated_id_123","20141122",expectedProperties);
  }

  protected static final Event EVENT_NO_USER_ID = constructExpectedEventNoUserId();
  protected static final String JSON_EVENT_NO_USER_ID =
      "  {\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"event_type\": \"email_sent\"\n"
          + "  }";

  private static Event constructExpectedEventNoUserId() {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    return new Event("email_sent",null,"20141122",expectedProperties);
  }

  protected static Collection<Event> ALL_EVENTS;

  protected static final String ALL_JSON_EVENTS =
      "[\n"
          + JSON_EVENT_1_PROPERTY + ",\n" + JSON_EVENT_2_PROPERTIES + ",\n" + JSON_EVENT_0_PROPERTIES + ",\n"
          + JSON_EVENT_NO_DATE + ",\n" + JSON_EVENT_NO_EVENT_TYPE + ",\n" + JSON_EVENT_NO_USER_ID
          + "]\n";

  protected static Map<String, Event> jsonToEventMap;
  protected static Map<Event,String > eventToJsonMap;

  static {
    initializeEventMaps();
    ALL_EVENTS = jsonToEventMap.values();
  }

  protected static void initializeEventMaps() {
    initializeJsonToEventMap();
    initializeEventToJsonMap();
  }

  protected static void initializeJsonToEventMap() {
    jsonToEventMap = new HashMap<String, Event>();
    jsonToEventMap.put(JSON_EVENT_1_PROPERTY, EVENT_1_PROPERTY);
    jsonToEventMap.put(JSON_EVENT_2_PROPERTIES, EVENT_2_PROPERTIES);
    jsonToEventMap.put(JSON_EVENT_0_PROPERTIES, EVENT_0_PROPERTIES);
    jsonToEventMap.put(JSON_EVENT_NO_DATE, EVENT_NO_DATE);
    jsonToEventMap.put(JSON_EVENT_NO_EVENT_TYPE, EVENT_NO_EVENT_TYPE);
    jsonToEventMap.put(JSON_EVENT_NO_USER_ID, EVENT_NO_USER_ID);
  }

  protected static void initializeEventToJsonMap() {
    eventToJsonMap = new HashMap<Event, String>();
    eventToJsonMap.put(EVENT_1_PROPERTY, JSON_EVENT_1_PROPERTY);
    eventToJsonMap.put(EVENT_2_PROPERTIES, JSON_EVENT_2_PROPERTIES);
    eventToJsonMap.put(EVENT_0_PROPERTIES, JSON_EVENT_0_PROPERTIES);
    eventToJsonMap.put(EVENT_NO_DATE, JSON_EVENT_NO_DATE);
    eventToJsonMap.put(EVENT_NO_EVENT_TYPE, JSON_EVENT_NO_EVENT_TYPE);
    eventToJsonMap.put(EVENT_NO_USER_ID, JSON_EVENT_NO_USER_ID);
  }

  protected static final Comparator<Event> eventComparator = new Comparator<Event>() {
    @Override public int compare(final Event event, final Event event2) {
      return event.hashCode() - event2.hashCode();
    }
  };
}
