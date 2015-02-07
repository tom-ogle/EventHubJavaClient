package com.github.eventhubjavaclient.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static com.github.eventhubjavaclient.EventHubClientUtils.EVENT_HUB_DATE_FORMATTER;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
  public static List<Event> ALL_EVENTS_SORTED_IN_JSON_ORDER;

  public static final String ALL_JSON_EVENTS =
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

  public static final Comparator<Event> eventComparator = new Comparator<Event>() {
    @Override public int compare(final Event event, final Event event2) {
      return event.hashCode() - event2.hashCode();
    }
  };

  public static void assertThatListsOfEventsHaveSameValues(final List<Event> expectedEvents, final List<Event> actualEvents) {
    if(expectedEvents==null || actualEvents==null) {
      assertNull("actualEvents was null but expectedEvents wasn't",expectedEvents);
      assertNull("expectedEvents was null but actualEvents wasn't",actualEvents);
      return;
    }
    int expectedEventsLength = expectedEvents.size();
    int actualEventsLength = actualEvents.size();
    assertEquals(format("Expected events length was %d but actual was %d",expectedEventsLength,actualEventsLength),
        expectedEventsLength,actualEventsLength);

    Collections.sort(actualEvents,EventSerializationTestBase.eventComparator);
    Collections.sort(expectedEvents,EventSerializationTestBase.eventComparator);
    for(int i = 0; i < expectedEventsLength; i++) {
      Event expectedEvent = expectedEvents.get(i);
      Event actualEvent = actualEvents.get(i);
      assertThatEventsHaveSameValues(expectedEvent,actualEvent);
    }
  }

  protected static void assertThatEventsHaveSameValues(final Event expectedEvent, final Event actualEvent) {
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
