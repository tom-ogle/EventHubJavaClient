package com.github.eventhubjavaclient.event;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.Map;

import static com.github.eventhubjavaclient.EventHubClientUtils.EVENT_HUB_DATE_FORMATTER;
import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_DATE;
import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_EVENT_TYPE;
import static com.github.eventhubjavaclient.event.EventDeserializer.KEY_EXTERNAL_USER_ID;

public class EventSerializer implements JsonSerializer<Event> {

  @Override public JsonElement serialize(final Event event, final Type type, final JsonSerializationContext jsonSerializationContext) {
    JsonObject eventJson = new JsonObject();
    addIfPropertyNotNull(eventJson, KEY_EVENT_TYPE, event.getEventType());
    addIfPropertyNotNull(eventJson, KEY_EXTERNAL_USER_ID, event.getExternalUserId());
    DateTime date = event.getDate();
    if(date!=null)
      addIfPropertyNotNull(eventJson, KEY_DATE, date.toString(EVENT_HUB_DATE_FORMATTER));
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
