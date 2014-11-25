package com.github.eventhubjavaclient.event;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EventDeserializerTest {

  private Gson gson;

  private static final String JSON_EVENT_1 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"submission\",\n"
      + "    \"exercise\": \"homepage_1\",\n"
      + "    \"external_user_id\": \"generated_id_123\"\n"
      + "  }";
  @Test
  public void testEvent1() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("exercise","homepage_1");
    Event expectedEvent = new Event("submission","generated_id_123","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_1,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_2 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"submission\",\n"
      + "    \"exercise\": \"homepage_2\",\n"
      + "    \"external_user_id\": \"generated_id_123\"\n"
      + "  }";

  @Test
  public void testEvent2() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("exercise","homepage_2");
    Event expectedEvent = new Event("submission","generated_id_123","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_2,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_3 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"signup\",\n"
      + "    \"experiment\": \"signup_v1\",\n"
      + "    \"external_user_id\": \"generated_id_123\",\n"
      + "    \"treatment\": \"control\"\n"
      + "  }";
  @Test
  public void testEvent3() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("experiment","signup_v1");
    expectedProperties.put("treatment","control");
    Event expectedEvent = new Event("signup","generated_id_123","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_3,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_4 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"pageview\",\n"
      + "    \"external_user_id\": \"chengtao@codecademy.com\",\n"
      + "    \"page\": \"tracks\"\n"
      + "  }";
  @Test
  public void testEvent4() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("page","tracks");
    Event expectedEvent = new Event("pageview","chengtao@codecademy.com","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_4,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_5 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"start_track\",\n"
      + "    \"external_user_id\": \"chengtao@codecademy.com\",\n"
      + "    \"track\": \"javascript\"\n"
      + "  }";
  @Test
  public void testEvent5() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("track","javascript");
    Event expectedEvent = new Event("start_track","chengtao@codecademy.com","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_5,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_6 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"submission\",\n"
      + "    \"exercise\": \"javascript_1\",\n"
      + "    \"external_user_id\": \"chengtao@codecademy.com\"\n"
      + "  }";
  @Test
  public void testEvent6() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("exercise","javascript_1");
    Event expectedEvent = new Event("submission","chengtao@codecademy.com","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_6,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_7 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"email_sent\",\n"
      + "    \"external_user_id\": \"generated_id_123\"\n"
      + "  }";
  @Test
  public void testEvent7() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    Event expectedEvent = new Event("email_sent","generated_id_123","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_7,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_8 = "  {\n"
      + "    \"date\": \"20141111\",\n"
      + "    \"event_type\": \"email_sent\",\n"
      + "    \"external_user_id\": \"generated_id_123\"\n"
      + "  }";
  @Test
  public void testEvent8() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    Event expectedEvent = new Event("email_sent","generated_id_123","20141111",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_8,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_9 =
        "  {\n"
      + "    \"date\": \"20141111\",\n"
      + "    \"event_type\": \"email_sent\",\n"
      + "    \"external_user_id\": \"generated_id_123\",\n"
      + "    \"testparam1\": \"value1\",\n"
      + "    \"testparam2\": \"value2\"\n"
      + "  }";
  @Test
  public void testEvent9() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("testparam1","value1");
    expectedProperties.put("testparam2","value2");
    Event expectedEvent = new Event("email_sent","generated_id_123","20141111",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_9,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }
  private static final String JSON_EVENT_10 =
        "  {\n"
      + "    \"date\": \"20141122\",\n"
      + "    \"event_type\": \"email_sent\",\n"
      + "    \"external_user_id\": \"generated_id_123\",\n"
      + "    \"testparam1\": \"value1\",\n"
      + "    \"testparam2\": \"value2\"\n"
      + "  }\n";
  @Test
  public void testEvent10() throws Exception {
    Map<String,String> expectedProperties = new HashMap<String, String>();
    expectedProperties.put("testparam1","value1");
    expectedProperties.put("testparam2","value2");
    Event expectedEvent = new Event("email_sent","generated_id_123","20141122",expectedProperties);
    Event actualEvent = gson.fromJson(JSON_EVENT_10,Event.class);
    assertEquals(expectedEvent,actualEvent);
  }

  private static final String ALL_JSON_EVENTS =
      "[\n"
          + JSON_EVENT_1 + ",\n" + JSON_EVENT_2 + ",\n" + JSON_EVENT_3 + ",\n"
          + JSON_EVENT_4 + ",\n" + JSON_EVENT_5 + ",\n" + JSON_EVENT_6 + ",\n"
          + JSON_EVENT_7 + ",\n" + JSON_EVENT_8 + ",\n" + JSON_EVENT_9 + ",\n"
          + JSON_EVENT_10
      + "]\n";

  @Before
  public void setUp() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
    gson = gsonBuilder.create();
  }

  @Test
  public void testAllJsonEvents() throws Exception {
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    Collection<Event> userEvents = gson.fromJson(ALL_JSON_EVENTS,collectionType);
  }


}
