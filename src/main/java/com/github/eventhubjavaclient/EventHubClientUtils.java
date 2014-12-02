package com.github.eventhubjavaclient;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 */
public class EventHubClientUtils {
  public static DateTimeFormatter EVENT_HUB_DATE_FORMATTER = DateTimeFormat.forPattern("yyyyMMdd");
}
