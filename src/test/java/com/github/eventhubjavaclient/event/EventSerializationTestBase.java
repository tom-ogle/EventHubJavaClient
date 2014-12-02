package com.github.eventhubjavaclient.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.junit.Before;

import java.util.*;

/**
 *
 */
public class EventSerializationTestBase {

  private static final DateTime DATE = new DateTime(2014,11,22,0,0);

  @Before
  public void setUp() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
    gsonBuilder.registerTypeAdapter(Event.class, new EventSerializer());
    gson = gsonBuilder.create();
  }

  protected Gson gson;

  protected static final Event EVENT_1_PROPERTY = constructExpectedEvent1Property();
  protected static final String JSON_EVENT_1_PROPERTY =
      "  {\n"
          + "    \"event_type\": \"submission\",\n"
          + "    \"external_user_id\": \"generated_id_123\",\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"exercise\": \"homepage_1\"\n"
          + "  }";

  private static Event constructExpectedEvent1Property() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    expectedProperties.put("exercise","homepage_1");
    return new Event("submission","generated_id_123",DATE,expectedProperties);
  }

  protected static final Event EVENT_2_PROPERTIES = constructExpectedEvent2Properties();
  protected static final String JSON_EVENT_2_PROPERTIES =
      "  {\n"
          + "    \"event_type\": \"signup\",\n"
          + "    \"external_user_id\": \"generated_id_123\",\n"
          + "    \"date\": \"20141122\",\n"
          + "    \"experiment\": \"signup_v1\",\n"
          + "    \"treatment\": \"control\"\n"
          + "  }";

  private static Event constructExpectedEvent2Properties() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    expectedProperties.put("experiment","signup_v1");
    expectedProperties.put("treatment","control");
    return new Event("signup","generated_id_123",DATE,expectedProperties);
  }

  protected static final Event EVENT_0_PROPERTIES = constructExpectedEvent0Properties();
  protected static final String JSON_EVENT_0_PROPERTIES =
      "  {\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\",\n"
          + "    \"date\": \"20141122\"\n"
          + "  }";

  private static Event constructExpectedEvent0Properties() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    return new Event("email_sent","generated_id_123",DATE,expectedProperties);
  }

  protected static final Event EVENT_NO_DATE = constructExpectedEventNoDate();
  protected static final String JSON_EVENT_NO_DATE =
      "  {\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"external_user_id\": \"generated_id_123\"\n"
          + "  }";

  private static Event constructExpectedEventNoDate() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    return new Event("email_sent","generated_id_123",null,expectedProperties);
  }

  protected static final Event EVENT_NO_EVENT_TYPE = constructExpectedEventNoEventType();
  protected static final String JSON_EVENT_NO_EVENT_TYPE =
      "  {\n"
          + "    \"external_user_id\": \"generated_id_123\",\n"
          + "    \"date\": \"20141122\"\n"
          + "  }";

  private static Event constructExpectedEventNoEventType() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    return new Event(null,"generated_id_123",DATE,expectedProperties);
  }

  protected static final Event EVENT_NO_USER_ID = constructExpectedEventNoUserId();
  protected static final String JSON_EVENT_NO_USER_ID =
      "  {\n"
          + "    \"event_type\": \"email_sent\",\n"
          + "    \"date\": \"20141122\"\n"
          + "  }";

  private static Event constructExpectedEventNoUserId() {
    Map<String,String> expectedProperties = new TreeMap<String, String>();
    return new Event("email_sent",null,DATE,expectedProperties);
  }

//  protected static Collection<Event> ALL_EVENTS;
  protected static List<Event> ALL_EVENTS_SORTED_IN_JSON_ORDER;

  protected static final String ALL_JSON_EVENTS =
      "[\n"
          + JSON_EVENT_1_PROPERTY + ",\n" + JSON_EVENT_2_PROPERTIES + ",\n" + JSON_EVENT_0_PROPERTIES + ",\n"
          + JSON_EVENT_NO_DATE + ",\n" + JSON_EVENT_NO_EVENT_TYPE + ",\n" + JSON_EVENT_NO_USER_ID
          + "]\n";

  static {
    initializeEventsInJsonOrder();
  }

  private static void initializeEventsInJsonOrder() {
    ALL_EVENTS_SORTED_IN_JSON_ORDER = new ArrayList<Event>();
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_1_PROPERTY);
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_2_PROPERTIES);
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_0_PROPERTIES);
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_NO_DATE);
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_NO_EVENT_TYPE);
    ALL_EVENTS_SORTED_IN_JSON_ORDER.add(EVENT_NO_USER_ID);
  }

  protected static final Comparator<Event> eventComparator = new Comparator<Event>() {
    @Override public int compare(final Event event, final Event event2) {
      return event.hashCode() - event2.hashCode();
    }
  };
}
