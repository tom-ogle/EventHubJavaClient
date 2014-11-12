package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.ws.rs.core.MediaType;

/**
 *
 */
public class EventHubClient {

  // Factory methods

  public static EventHubClient createDefaultClient(String baseUrl) {
    ClientConfig config = new DefaultClientConfig();
    return new EventHubClient(baseUrl, config);
  }

  /**
   * Creates a custom EventHubClient with the provided config
   * @param baseUrl The base URL of the EventHub server e.g. http://localhost:<portnumber>
   * @param config The configuration to provide to provide to the client
   * @return The created client, using the provided URL and config
   * @return The created client, using the provided URL and config
   */
  public static EventHubClient createCustomClient(String baseUrl, ClientConfig config) {
    return new EventHubClient(baseUrl, config);
  }

  // Static

  private static final String USER_KEYS_PATH = "/users/keys";
  private static final String USER_VALUES_PATH = "/users/values";
  private static final String EVENT_KEYS_PATH = "/events/keys";
  private static final String EVENT_TYPES_PATH = "/events/types";
  private static final String EVENT_VALUES_PATH = "/events/values";
  private static final String EVENT_FUNNEL_PATH = "/events/funnel";

  private static final int[] OK_RESPONSE = new int[] {200};

  // Instance

  private static DateTimeFormatter eventHubDateFormatter = DateTimeFormat.forPattern("yyyyMMdd");
  private WebResource webResource;
  private Gson gson = new Gson();

  private EventHubClient(String baseUrl, ClientConfig config) {
    Client client = Client.create(config);
    webResource = client.resource(baseUrl);
  }

  // Users

  /**
   * Gets all key-value keys over all the users.
   * @return The user keys, guaranteed not null.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array
   */
  public String[] getUserKeys() throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(USER_KEYS_PATH).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String body = response.getEntity(String.class);
    return extractStringArray(body);
  }

  /**
   * Gets all key-value values over all the users.
   * @param userKey
   * @return
   * @throws UnexpectedResponseCodeException
   * @throws BadlyFormedResponseBodyException
   */
  public String[] getUserValues(final String userKey) throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    return getUserValues(userKey,null);
  }

  public String[] getUserValues(final String userKey, final String prefix)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    checkNotNull(userKey);
    WebResource request = webResource.path(USER_VALUES_PATH)
                                     .queryParam("user_key",userKey);
    if(prefix!=null)
      request = request.queryParam("prefix",prefix);

    ClientResponse response = request.get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String body = response.getEntity(String.class);
    return extractStringArray(body);
  }

  // Events

  public String[] getEventKeys(final String eventType) throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(EVENT_KEYS_PATH)
                                         .queryParam("event_type",eventType)
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String body = response.getEntity(String.class);
    return extractStringArray(body);
  }

  public String[] getEventTypes() throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(EVENT_TYPES_PATH).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String body = response.getEntity(String.class);
    return extractStringArray(body);
  }

  public String[] getEventValues(final String eventType, final String eventKey)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    return getEventValues(eventType,eventKey,null);
  }

  public String[] getEventValues(final String eventType, final String eventKey, final String prefix)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    WebResource request = webResource.path(EVENT_VALUES_PATH)
                                     .queryParam("event_type",eventType)
                                     .queryParam("event_key",eventKey);
    if(prefix!=null)
      request = request.queryParam("prefix",prefix);

    ClientResponse response = request.get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String body = response.getEntity(String.class);
    return extractStringArray(body);
  }

  // Event funnel

  public int[] getEventFunnelCounts(final DateTime startDate, final DateTime endDate, final String[] funnelSteps,
      final int daysToCompleteFunnel) throws BadlyFormedResponseBodyException {

    String body = produceFunnelCountsBody(startDate,endDate,funnelSteps,daysToCompleteFunnel);
    ClientResponse response = webResource.path(EVENT_FUNNEL_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .post(ClientResponse.class, body);
    String responseBody = response.getEntity(String.class);
    return gson.fromJson(responseBody,int[].class);
  }

  // Utils

  private static String produceFunnelCountsBody(final DateTime startDate, final DateTime endDate, final String[] funnelSteps
      ,final int daysToCompleteFunnel) {
    StringBuilder sb = new StringBuilder();
    sb.append("start_date=").append(startDate.toString(eventHubDateFormatter))
      .append("&end_date=").append(endDate.toString(eventHubDateFormatter));
    for(String funnelStep : funnelSteps) {
      sb.append("&funnel_steps[]=").append(funnelStep);
    }
    sb.append("&num_days_to_complete_funnel=").append(daysToCompleteFunnel);
    return sb.toString();
  }

  private String[] extractStringArray(final String body) throws BadlyFormedResponseBodyException {
    if(body == null || "".equals(body))
      throw new BadlyFormedResponseBodyException("Response body was null");
    String[] result = gson.fromJson(body, String[].class);
    if(result==null)
      throw new BadlyFormedResponseBodyException("Could not extract array from response body");
    return result;
  }

  private void checkResponseCode(ClientResponse response, int[] expectedStatusArray) throws UnexpectedResponseCodeException {
    int actualStatus = response.getStatus();
    for(int status : expectedStatusArray) {
      if(status == actualStatus)
        return;
    }
    throw new UnexpectedResponseCodeException(expectedStatusArray,actualStatus);
  }

  private static void checkNotNull(final Object obj) {
    if(obj==null)
      throw new NullPointerException("Expecting non null value");
  }
}
