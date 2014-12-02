package com.github.eventhubjavaclient.event;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.eventhubjavaclient.EventHubClientUtils.EVENT_HUB_DATE_FORMATTER;

public class EventDeserializer implements JsonDeserializer<Event> {

  public static final String KEY_EVENT_TYPE = "event_type";
  public static final String KEY_EXTERNAL_USER_ID = "external_user_id";
  public static final String KEY_DATE = "date";

  @Override public Event deserialize(final JsonElement jsonElement, final Type type,
      final JsonDeserializationContext jsonDeserializationContext)
      throws JsonParseException {
    final Map<String, String> properties = new HashMap<String, String>();
    String eventType = null;
    String externalUserId = null;
    DateTime date = null;
    Set<Map.Entry<String, JsonElement>> entries = jsonElement.getAsJsonObject().entrySet();
    for(Map.Entry<String, JsonElement> entry : entries) {
      String key = entry.getKey();
      String value = entry.getValue().getAsString();
      if(KEY_EVENT_TYPE.equals(key))
        eventType = value;
      else if(KEY_EXTERNAL_USER_ID.equals(key))
        externalUserId = value;
      else if(KEY_DATE.equals(key))
        date = EVENT_HUB_DATE_FORMATTER.parseDateTime(value);
      else
        properties.put(key,value);
    }
    return new Event(eventType,externalUserId,date,properties);
  }
}
