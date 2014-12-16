package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Ignore
public class EventHubClientExploratoryTest  {

  @Test
  public void tempTest() throws Exception {
    EventHubClient client = EventHubClient.createDefaultClient("http://localhost:8080");

    try {
      // Events
      String[] eventTypes = client.getEventTypes();
      String[] eventKeys = client.getEventKeys(eventTypes[1]);
      String[] eventValues = client.getEventValues(eventTypes[1],eventKeys[0]);
      List<Event> events = new ArrayList<Event>();
      Event event = new Event("signup", "chengtao1@codecademy.com", null);
      Event event2 = new Event("pageview", "chengtao1@codecademy.com", null);
      events.add(event);
      events.add(event2);
      client.batchTrackEvent(events);
      // Users
      String[] userKeys = client.getUserKeys();
      String[] userValues = client.getUserValues(userKeys[0]);
      String[] userValuesBPrefix = client.getUserValues(userKeys[0],"b");
      String[] userValuesXPrefix = client.getUserValues(userKeys[0],"x");

      // Funnel
      int[] funnelCounts =
          client.retrieveEventFunnelCounts(new DateTime(2014, 11, 11, 0, 0), new DateTime(2014, 11, 25, 0, 0),
              new String[] { "pageview", "signup", "submission" }, 7);
      Map<String, String> userFields = new HashMap<String, String>();
      userFields.put("my_key","my_field");
      client.addOrUpdateUser("my_user1", userFields);
      client.aliasUser("another_user_alias", "my_user1");
      client.getUserTimeline("chengtao@codecademy.com", 1, 10);
      Map<String,String> userFilters = new HashMap<String, String>();
      userFilters.put("external_user_id", "chengtao1@codecademy.com");
      List<String> users = client.getUsers(userFilters);
      String serverStats = client.getServerStats();
      System.out.printf(serverStats);
      Map<String,String> rowFilters = new HashMap<String, String>();
      rowFilters.put("experiment", "signup_v1");
      rowFilters.put("treatment", "control");
      int[][] eventCohortTable = client.retrieveEventCohortTable(new DateTime(2014, 11, 11, 0, 0), new DateTime(2014, 11, 25, 0, 0),
          "signup","submission",7,2,rowFilters,null);

      Map<String, String> properties = new HashMap<String, String>();
      properties.put("experiment","signup_v50");
      event = new Event("myeventtype","chengtao1@codecademy.com",properties);
      client.trackEvent(event);
      System.out.printf("");
    } catch(UnexpectedResponseCodeException e) {
      System.out.printf("Actual response code was: "+e.getActualCode());
    }


  }

}