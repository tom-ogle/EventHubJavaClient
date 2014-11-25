package com.github.eventhubjavaclient.event;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

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
    eventJson.add("event_type",new JsonPrimitive(event.getEventType()));
    eventJson.add("external_user_id", new JsonPrimitive(event.getExternalUserId()));
    for(Map.Entry<String, String> entry : event.getPropertyEntrySet()) {
      eventJson.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
    }
    return eventJson;
  }
}
