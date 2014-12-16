package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.event.EventDeserializer;
import com.github.eventhubjavaclient.event.EventSerializer;
import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.joda.time.DateTime;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static com.github.eventhubjavaclient.EventHubClientUtils.EVENT_HUB_DATE_FORMATTER;
/**
 *
 */
public class EventHubClient {

  // Static

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

  private static final String USER_KEYS_PATH = "/users/keys";
  private static final String USER_VALUES_PATH = "/users/values";
  private static final String USER_ALIAS_PATH = "/users/alias";
  private static final String USER_ADD_OR_UPDATE_PATH = "/users/add_or_update";
  private static final String USER_TIMELINE_PATH = "/users/timeline";
  private static final String USER_FIND_PATH = "/users/find";

  private static final String EVENT_KEYS_PATH = "/events/keys";
  private static final String EVENT_TYPES_PATH = "/events/types";
  private static final String EVENT_VALUES_PATH = "/events/values";
  private static final String EVENT_FUNNEL_PATH = "/events/funnel";
  private static final String EVENT_TRACK_PATH = "/events/track";
  private static final String EVENT_BATCH_TRACK_PATH = "/events/batch_track";
  private static final String EVENT_COHORT_PATH = "/events/cohort";

  private static final String SERVER_STATS_PATH = "/varz";

  private static final int[] OK_RESPONSE = new int[] {200};

  // Instance

  private WebResource webResource;
  private Gson gson;

  private EventHubClient(String baseUrl, ClientConfig config) {
    Client client = Client.create(config);
    webResource = client.resource(baseUrl);
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(Event.class, new EventDeserializer());
    gsonBuilder.registerTypeAdapter(Event.class, new EventSerializer());
    gson = gsonBuilder.create();
  }

  // Users

  /**
   * Adds the user if they don't exist and updates their properties if they do exist.
   * @param userId The userID to update, must be NotNull
   * @param userFieldValueMap The key-value pairs to attach to this user.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   */
  public void addOrUpdateUser(final String userId, final Map<String,String> userFieldValueMap) throws UnexpectedResponseCodeException {
    WebResource resource = webResource.path(USER_ADD_OR_UPDATE_PATH)
                                         .queryParam("external_user_id", userId);
    if(userFieldValueMap!=null) {
      for(Map.Entry<String, String> entry : userFieldValueMap.entrySet()) {
        resource = resource.queryParam(entry.getKey(), entry.getValue());
      }
    }
    ClientResponse response = resource.post(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
  }

  /**
   * Gets all key-value keys over all the users.
   * @return The user keys, guaranteed not null.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array
   */
  public String[] getUserKeys() throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(USER_KEYS_PATH)
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
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
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
  }

  /**
   *
   * @param mapFromNewUserName The new name to map from. Must be NotNull.
   * @param mapToExistingUserName The old user name to map to. Must be NotNull.
   * @throws UnexpectedResponseCodeException
   */
  public void aliasUser(final String mapFromNewUserName, final String mapToExistingUserName) throws UnexpectedResponseCodeException {
    ClientResponse response = webResource.path(USER_ALIAS_PATH)
                                         .queryParam("from_external_user_id", mapFromNewUserName)
                                         .queryParam("to_external_user_id",mapToExistingUserName)
                                         .accept(MediaType.WILDCARD_TYPE)
                                         .post(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
  }

  public Collection<Event> getUserTimeline(final String userName, final int offset, final int numberOfRecords)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(USER_TIMELINE_PATH)
                                         .queryParam("external_user_id",userName)
                                         .queryParam("offset", Integer.toString(offset))
                                         .queryParam("num_records", Integer.toString(numberOfRecords))
                                         .get(ClientResponse.class);

    checkResponseCode(response,OK_RESPONSE);
    String entity = response.getEntity(String.class);
    checkForBadBody(entity);
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    return gson.fromJson(entity,collectionType);
  }

  public List<String> getUsers(Map<String, String> filters) throws UnexpectedResponseCodeException {
    String body = produceFiltersRequestBody(filters, "ufk[]", "ufv[]");
    ClientResponse response = webResource.path(USER_FIND_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class,body);
    checkResponseCode(response,OK_RESPONSE);
    return extractUserNames(response.getEntity(String.class));

  }

  // Events

  public String[] getEventKeys(final String eventType) throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(EVENT_KEYS_PATH)
                                         .queryParam("event_type",eventType)
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
  }

  public String[] getEventTypes() throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(EVENT_TYPES_PATH).accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
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
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
  }

  /**
   * Tracks the given event
   * @param event The event to track, must be NotNull
   * @throws UnexpectedResponseCodeException Thrown if EventHub returns a non-OK response
   */
  public void trackEvent(final Event event) throws UnexpectedResponseCodeException {
    WebResource resource = webResource.path(EVENT_TRACK_PATH)
                                      .queryParam("event_type", event.getEventType())
                                      .queryParam("external_user_id", event.getExternalUserId());
    DateTime date = event.getDate();
    if(date!=null)
      resource = resource.queryParam("date", date.toString(EVENT_HUB_DATE_FORMATTER));
    Map<String,String> additionalParams = event.getUnmodifiablePropertyMap();
    if(additionalParams!=null) {
      for(Map.Entry<String, String> entry : additionalParams.entrySet()) {
        resource = resource.queryParam(entry.getKey(),entry.getValue());
      }
    }
    ClientResponse response = resource.header("Content-Type", "application/x-www-form-urlencoded")
                                      .accept(MediaType.APPLICATION_JSON_TYPE)
                                      .post(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
  }

  public void batchTrackEvent(final List<Event> events) throws UnexpectedResponseCodeException {
    final String requestBody = produceBatchEventsBody(events);
    ClientResponse response = webResource.path(EVENT_BATCH_TRACK_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class, requestBody);
    checkResponseCode(response,OK_RESPONSE);
  }

  // Event cohort

  public int[][] retrieveEventCohortTable(final DateTime startDate, final DateTime endDate, final String rowEventType,
      final String columnEventType, final int numberOfDaysPerRow, final int numberOfDaysPerColumn, final Map<String, String> rowfilters,
      final Map<String, String> columnFilters) throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    String body = produceEventCohortTableRequestBody(startDate,endDate,rowEventType,columnEventType,numberOfDaysPerRow,
        numberOfDaysPerColumn,rowfilters,columnFilters);
    ClientResponse response = webResource.path(EVENT_COHORT_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class, body);
    checkResponseCode(response,OK_RESPONSE);
    String entity = response.getEntity(String.class);
    return  extractFromBody(entity,int[][].class);
  }

  // Event funnel

  public int[] retrieveEventFunnelCounts(final DateTime startDate, final DateTime endDate, final String[] funnelSteps,
      final int daysToCompleteFunnel) throws BadlyFormedResponseBodyException, UnexpectedResponseCodeException {

    String body = produceEventFunnelCountsRequestBody(startDate, endDate, funnelSteps, daysToCompleteFunnel);
    ClientResponse response = webResource.path(EVENT_FUNNEL_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .post(ClientResponse.class, body);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,int[].class);
  }

  public String getServerStats() throws UnexpectedResponseCodeException {
    ClientResponse response = webResource.path(SERVER_STATS_PATH)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    return response.getEntity(String.class);
  }

  // Utils


  private String produceBatchEventsBody(final List<Event> events) {
    StringBuilder sb = new StringBuilder("events=");
    Type collectionType = new TypeToken<Collection<Event>>(){}.getType();
    sb.append(gson.toJson(events, collectionType));
    return sb.toString();
  }

  private String produceEventCohortTableRequestBody(final DateTime startDate, final DateTime endDate, final String rowEventType,
      final String columnEventType, final int numberOfDaysPerRow, final int numberOfColumns, final Map<String, String> rowFilters,
      final Map<String, String> columnFilters) {
    StringBuilder sb = produceDateRangeRequestBody(startDate,endDate,new StringBuilder());;
    sb.append("&row_event_type=").append(rowEventType);
    sb.append("&column_event_type=").append(columnEventType);
    sb.append("&num_days_per_row=").append(numberOfDaysPerRow);
    sb.append("&num_columns=").append(numberOfColumns);
    if(rowFilters!=null && rowFilters.size()>0)
      sb.append("&").append(produceFiltersRequestBody(rowFilters, "refk[]", "refv[]"));
    if(columnFilters!=null && columnFilters.size()>0)
      sb.append("&").append(produceFiltersRequestBody(columnFilters, "cefk[]", "cefv[]"));
    return sb.toString();
  }

  private static String produceFiltersRequestBody(final Map<String, String> filters, final String keyArrayName, final String valueArrayName) {
    StringBuilder userFilterKeys = new StringBuilder();
    StringBuilder userFilterValues = new StringBuilder();
    for(Map.Entry<String, String> entry : filters.entrySet()) {
      userFilterKeys.append(keyArrayName).append("=").append(entry.getKey()).append("&");
      userFilterValues.append(valueArrayName).append("=").append(entry.getValue()).append("&");
    }
    userFilterValues.deleteCharAt(userFilterValues.length()-1);
    return userFilterKeys.toString()+userFilterValues.toString();
  }

  private static String produceEventFunnelCountsRequestBody(final DateTime startDate, final DateTime endDate, final String[] funnelSteps
      , final int daysToCompleteFunnel) {
    StringBuilder sb = produceDateRangeRequestBody(startDate,endDate,new StringBuilder());;
    for(String funnelStep : funnelSteps) {
      sb.append("&funnel_steps[]=").append(funnelStep);
    }
    sb.append("&num_days_to_complete_funnel=").append(daysToCompleteFunnel);
    return sb.toString();
  }

  private static StringBuilder produceDateRangeRequestBody(final DateTime startDate, final DateTime endDate, final StringBuilder sb) {
    sb.append("start_date=").append(startDate.toString(EVENT_HUB_DATE_FORMATTER))
      .append("&end_date=").append(endDate.toString(EVENT_HUB_DATE_FORMATTER));
    return sb;
  }

  private <T> T extractFromBody(final String body, Class<T> clazz) throws BadlyFormedResponseBodyException {
    checkForBadBody(body);
    T result = null;
    try {
      result = gson.fromJson(body, clazz);
    } catch(JsonSyntaxException e) {
      throw new BadlyFormedResponseBodyException("Badly formed response",e);
    }
    if(result==null)
      throw new BadlyFormedResponseBodyException("Could not extract from response body");
    return result;
  }

  private void checkForBadBody(final String body) throws BadlyFormedResponseBodyException {
    if(body == null || "".equals(body))
      throw new BadlyFormedResponseBodyException("Response body was null");
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

  private List<String> extractUserNames(final String json) {
    List<String> names = new ArrayList<String>();
    JsonArray array = gson.fromJson(json,JsonArray.class);
    for(JsonElement jsonElement : array) {
      JsonObject object = jsonElement.getAsJsonObject();
      names.add(object.get("external_user_id").getAsString());
    }
    return names;
  }
}
