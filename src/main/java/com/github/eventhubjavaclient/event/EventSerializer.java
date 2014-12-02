package com.github.eventhubjavaclient.event;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_EVENT_TYPE;
import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_EXTERNAL_USER_ID;
import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_DATE;

/**
 *{
 external_user_id: 'chengtao1@codecademy.com',
 event_type: 'pageview',
 experiment: 'homepage_v1',
 treatment: 'new console',
 page: 'home'
 }
 */
public class EventSerializer implements JsonSerializer<Event> {

  @Override public JsonElement serialize(final Event event, final Type type, final JsonSerializationContext jsonSerializationContext) {
    JsonObject eventJson = new JsonObject();
    addIfPropertyNotNull(eventJson,KEY_EVENT_TYPE,event.getEventType());
    addIfPropertyNotNull(eventJson,KEY_EXTERNAL_USER_ID,event.getExternalUserId());
    addIfPropertyNotNull(eventJson,KEY_DATE,event.getDate());
    for(Map.Entry<String, String> entry : event.getPropertyEntrySet()) {
      eventJson.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
    }
    return eventJson;
  }

  private static void addIfPropertyNotNull(JsonObject eventJson, final String key, final String property) {
    if(property!=null)
      eventJson.add(key, new JsonPrimitive(property));
  }
}
