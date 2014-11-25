package com.github.eventhubjavaclient.event;

import java.util.*;

/**
 *
 */
public class Event {
  private final String eventType;
  private final String externalUserId;
  private String date;
  private Map<String, String> properties;

  public Event(String eventType, String externalUserId, String date, Map<String, String> properties) {
    this.eventType = eventType;
    this.externalUserId = externalUserId;
    this.date = date;
    setProperties(properties);
  }

  public Event(final String eventType, final String externalUserId, final Map<String, String> properties) {
    this.eventType = eventType;
    this.externalUserId = externalUserId;
    setProperties(properties);
  }

  public String getEventType() {
    return eventType;
  }

  public String getExternalUserId() {
    return externalUserId;
  }

  public String getDate() {
    return date;
  }

  public String putProperty(final String key, final String value) {
    return properties.put(key,value);
  }

  public String getProperty(final String key) {
    return properties.get(key);
  }

  public Set<String> getPropertyKeys() {
    return Collections.unmodifiableSet(properties.keySet());
  }

  public Collection<String> getPropertyValues() {
    return Collections.unmodifiableCollection(properties.values());
  }

  public Set<Map.Entry<String, String>> getPropertyEntrySet() {
    return Collections.unmodifiableSet(properties.entrySet());
  }

  @Override
  public boolean equals(final Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    Event event = (Event) o;

    if(date != null ? !date.equals(event.date) : event.date != null) {
      return false;
    }
    if(eventType != null ? !eventType.equals(event.eventType) : event.eventType != null) {
      return false;
    }
    if(externalUserId != null ? !externalUserId.equals(event.externalUserId) : event.externalUserId != null) {
      return false;
    }
    if(properties != null ? !properties.equals(event.properties) : event.properties != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = eventType != null ? eventType.hashCode() : 0;
    result = 31 * result + (externalUserId != null ? externalUserId.hashCode() : 0);
    result = 31 * result + (date != null ? date.hashCode() : 0);
    result = 31 * result + (properties != null ? properties.hashCode() : 0);
    return result;
  }

  private void setProperties(Map<String, String> properties) {
    if(properties!=null)
      this.properties = properties;
    else
      this.properties = new HashMap<String, String>();
  }
}
