package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class EventHubClientExploratoryTest  {

  @Test
  public void tempTest() throws Exception {
    EventHubClient client = EventHubClient.createDefaultClient("http://localhost:8080");

    try {
      // Events
      String[] eventTypes = client.getEventTypes();
      String[] eventKeys = client.getEventKeys(eventTypes[0]);
      String[] eventValues = client.getEventValues(eventTypes[0],eventKeys[0]);

      // Users
      String[] userKeys = client.getUserKeys();
      String[] userValues = client.getUserValues(userKeys[0]);
      String[] userValuesBPrefix = client.getUserValues(userKeys[0],"b");
      String[] userValuesXPrefix = client.getUserValues(userKeys[0],"x");

      System.out.printf("");
    } catch(UnexpectedResponseCodeException e) {
      System.out.printf("Actual response code was: "+e.getActualCode());
    }


  }

}