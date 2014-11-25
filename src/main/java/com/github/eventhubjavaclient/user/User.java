package com.github.eventhubjavaclient.user;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 *
 */
public class User {

  private String externalId;
  private Map<String, String> properties;

  public User(final String externalId, final Map<String, String> properties) {
    this.externalId = externalId;
    this.properties = properties;
  }

  public String getExternalId() {
    return externalId;
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
}
