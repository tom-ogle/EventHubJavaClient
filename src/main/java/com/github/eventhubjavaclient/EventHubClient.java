package com.github.eventhubjavaclient;

import com.github.eventhubjavaclient.event.Event;
import com.github.eventhubjavaclient.event.EventDeserializer;
import com.github.eventhubjavaclient.event.EventSerializer;
import com.github.eventhubjavaclient.exception.IllegalInputException;
import com.github.eventhubjavaclient.exception.BadlyFormedResponseBodyException;
import com.github.eventhubjavaclient.exception.UnexpectedResponseCodeException;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.joda.time.DateTime;

import javax.ws.rs.core.MediaType;
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

  /**
   * Creates a client instance with a default config.
   * @param baseUrl The base URL of the EventHub server e.g. http://localhost:<portnumber>
   * @return The created client, using the provided URL and default config
   */
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
   * @throws IllegalInputException Thrown if illegal input is provided (null userId).
   */
  public void addOrUpdateUser(final String userId, final Map<String,String> userFieldValueMap)
      throws UnexpectedResponseCodeException, IllegalInputException {
    checkNotNull(userId);
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
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array.
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
   * Gets all values for the given key from the set of all user key-value pairs (over every user).
   * @param userKey The key to find all values for.
   * @return All values for the given key, irrespective of which user the key-valiue pair is registered for.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array.
   * @throws IllegalInputException Thrown if illegal input is provided (null username).
   */
  public String[] getUserValues(final String userKey)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    return getUserValues(userKey, null);
  }

  /**
   * Gets all values for the given key from the set of all user key-value pairs (over every user), fltered using the given prefix.
   * @param userKey The key to find all values for.
   * @param prefix The prefix to filter the values by. No filter will be applied if set to null.
   * @return The filtered values for the given key, irrespective of which user the key-valiue pair is registered for.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array.
   * @throws IllegalInputException Thrown if illegal input is provided (null username).
   */
  public String[] getUserValues(final String userKey, final String prefix)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    checkNotNull(userKey);
    WebResource request = webResource.path(USER_VALUES_PATH)
                                     .queryParam("user_key",userKey);
    if(prefix!=null)
      request = request.queryParam("prefix",prefix);

    ClientResponse response = request.get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody, String[].class);
  }

  /**
   * Creates an alias for a user.
   * @param newUserName The new user name. Must be NotNull.
   * @param existingUserName The old user name to map the new name to. Must be NotNull.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   * @throws IllegalInputException Thrown if illegal input is provided (null new or existing username).
   */
  public void aliasUser(final String newUserName, final String existingUserName)
      throws UnexpectedResponseCodeException, IllegalInputException {
    checkNotNull(newUserName);
    checkNotNull(existingUserName);
    ClientResponse response = webResource.path(USER_ALIAS_PATH)
                                         .queryParam("from_external_user_id", newUserName)
                                         .queryParam("to_external_user_id",existingUserName)
                                         .accept(MediaType.WILDCARD_TYPE)
                                         .post(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
  }

  /**
   * Gets events for the given user.
   * @param userName The user to get the events for.
   * @param offset The offset in the server results to start from.
   * @param numberOfRecords The number of Event records to return.
   * @return The specified events for the given user as a collection of Events.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the response body was not a well-formed JSON array.
   * @throws IllegalInputException Thrown if illegal input is provided (null username).
   */
  public Collection<Event> getUserTimeline(final String userName, final int offset, final int numberOfRecords)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    checkNotNull(userName);
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

  /**
   * Gets all users that match the provided filters.
   * @param filters The filters to match. Must be NotNull.
   * @return A list of user names.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   * @throws BadlyFormedResponseBodyException
   */
  public List<String> getUsers(Map<String, String> filters)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    checkNotNull(filters);
    checkNotEmpty(filters);
    String body = produceFiltersRequestBody(filters, "ufk[]", "ufv[]");
    ClientResponse response = webResource.path(USER_FIND_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class,body);
    checkResponseCode(response,OK_RESPONSE);
    return extractUserNames(response.getEntity(String.class));

  }

  // Events

  /**
   * Gets the event keys for the provided event type.
   * @param eventType The event type to get all event keys for. Must be notNull.
   * @return An array of event keys as Strings.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the EventHub API returns a badly formed response.
   * @throws IllegalInputException Thrown if illegal input is provided (null event type).
   */
  public String[] getEventKeys(final String eventType)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    checkNotNull(eventType);
    ClientResponse response = webResource.path(EVENT_KEYS_PATH)
                                         .queryParam("event_type", eventType)
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,String[].class);
  }

  /**
   * Gets all event types.
   * @return An array of all event types, as Strings.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the EventhUb API returns a badly formed response.
   */
  public String[] getEventTypes() throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException {
    ClientResponse response = webResource.path(EVENT_TYPES_PATH)
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody, String[].class);
  }

  /**
   * Gets all event values for the given event type and key.
   * @param eventType The event type.
   * @param eventKey The event key.
   * @return Returns all event values for the given event type and key as an array of Strings.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the EventHub API returns a badly formed response.
   * @throws IllegalInputException Thrown if illegal input is provided (null event type).
   */
  public String[] getEventValues(final String eventType, final String eventKey)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    return getEventValues(eventType,eventKey,null);
  }

  /**
   * Gets all event values for the given event type and key with the given prefix.
   * @param eventType The event type.
   * @param eventKey The event key.
   * @param prefix The prefix to filter event values by.
   * @return Returns all event values for the given event type and key, with the given prefix, as an array of Strings.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws BadlyFormedResponseBodyException Thrown if the EventHub API returns a badly formed response.
   * @throws IllegalInputException Thrown if illegal input is provided (null event type or event key).
   */
  public String[] getEventValues(final String eventType, final String eventKey, final String prefix)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {
    checkNotNull(eventType);
    checkNotNull(eventKey);
    WebResource request = webResource.path(EVENT_VALUES_PATH)
                                     .queryParam("event_type", eventType)
                                     .queryParam("event_key",eventKey);
    if(prefix!=null)
      request = request.queryParam("prefix", prefix);

    ClientResponse response = request.get(ClientResponse.class);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody, String[].class);
  }

  /**
   * Tracks the given event.
   * @param event The event to track, must be NotNull
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   * @throws IllegalInputException Thrown if illegal input is provided (null event).
   */
  public void trackEvent(final Event event) throws UnexpectedResponseCodeException, IllegalInputException {
    checkNotNull(event);
    String eventType = event.getEventType();
    checkNotNull(eventType);
    String externalUserId = event.getExternalUserId();
    checkNotNull(externalUserId);

    WebResource resource = webResource.path(EVENT_TRACK_PATH)
                                      .queryParam("event_type", eventType)
                                      .queryParam("external_user_id", externalUserId);
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

  /**
   * Batch tracks all the provided events.
   * @param events The events to track. Must be NotNull and contain at least one event.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API
   * @throws IllegalInputException Thrown if illegal input is provided (null or empty events list).
   */
  public void batchTrackEvents(final List<Event> events) throws UnexpectedResponseCodeException, IllegalInputException {
    checkNotEmpty(events);
    final String requestBody = produceBatchEventsBody(events);
    ClientResponse response = webResource.path(EVENT_BATCH_TRACK_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class, requestBody);
    checkResponseCode(response,OK_RESPONSE);
  }

  // Event cohort

  public int[][] retrieveEventCohortTable(final DateTime startDate, final DateTime endDate, final String rowEventType,
      final String columnEventType, final int numberOfDaysPerRow, final int numberOfDaysPerColumn, final Map<String, String> rowFilters,
      final Map<String, String> columnFilters)
      throws UnexpectedResponseCodeException, BadlyFormedResponseBodyException, IllegalInputException {

    checkNotNull(startDate);
    checkNotNull(endDate);
    checkNotNull(rowEventType);
    checkNotNull(columnEventType);
    checkNotNull(rowFilters);
    checkNotNull(columnFilters);
    checkNotZero(numberOfDaysPerRow);
    checkNotZero(numberOfDaysPerColumn);

    String body = produceEventCohortTableRequestBody(startDate,endDate,rowEventType,columnEventType,numberOfDaysPerRow,
        numberOfDaysPerColumn,rowFilters,columnFilters);
    ClientResponse response = webResource.path(EVENT_COHORT_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .post(ClientResponse.class, body);
    checkResponseCode(response,OK_RESPONSE);
    String entity = response.getEntity(String.class);
    return  extractFromBody(entity,int[][].class);
  }

  // Event funnel

  public int[] retrieveEventFunnelCounts(final DateTime startDate, final DateTime endDate, final String[] funnelSteps,
      final int daysToCompleteFunnel) throws BadlyFormedResponseBodyException, UnexpectedResponseCodeException, IllegalInputException {
    checkNotNull(startDate);
    checkNotNull(endDate);
    checkNotEmpty(funnelSteps);
    if(daysToCompleteFunnel<1)
      throw new IllegalInputException("Expected days to complete funnel to be greater than 0, but was "+daysToCompleteFunnel);

    String body = produceEventFunnelCountsRequestBody(startDate, endDate, funnelSteps, daysToCompleteFunnel);
    ClientResponse response = webResource.path(EVENT_FUNNEL_PATH)
                                         .header("Content-Type", "application/x-www-form-urlencoded")
                                         .accept(MediaType.APPLICATION_JSON_TYPE)
                                         .post(ClientResponse.class, body);
    checkResponseCode(response,OK_RESPONSE);
    String responseBody = response.getEntity(String.class);
    return extractFromBody(responseBody,int[].class);
  }

  /**
   * Retrieves the server stats from the EventHub API.
   * @return The server stats, as a String.
   * @throws UnexpectedResponseCodeException Thrown if we got anything other than a 200 OK response from the EventHub API.
   */
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
    if(userFilterValues.length()!=0) {
      // Remove the last ampersand
      userFilterValues.deleteCharAt(userFilterValues.length() - 1);
    }
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

  private static void checkNotNull(final Object obj) throws IllegalInputException {
    if(obj==null)
      throw new IllegalInputException("Expecting non null value");
  }

  private static void checkNotEmpty(final Map map) throws IllegalInputException {
    if(map==null)
      throw new IllegalInputException("The provided map was null");
    if(map.size()<1)
      throw new IllegalInputException("The provided map was empty");
  }

  private static void checkNotEmpty(final List list) throws IllegalInputException {
    if(list==null)
      throw new IllegalInputException("The provided list was null");
    if(list.size()<1)
      throw new IllegalInputException("The provided list was empty");
  }

  private static <T> void checkNotEmpty(final T[] array) throws IllegalInputException {
    if(array==null)
      throw new IllegalInputException("The provided array was null");
    if(array.length<1)
      throw new IllegalInputException("The provided array was empty");
  }

  private static void checkNotZero(final int number) throws IllegalInputException {
    if(number == 0)
      throw new IllegalInputException("Zero is not a valid input value");
  }

  private List<String> extractUserNames(final String json) throws BadlyFormedResponseBodyException {
    List<String> names = new ArrayList<String>();
    JsonArray array = null;
    try {
      array = gson.fromJson(json, JsonArray.class);
    } catch (JsonSyntaxException e) {
      throw new BadlyFormedResponseBodyException("JSON in response body was badly formed", e);
    } catch (ClassCastException e) {
      throw new BadlyFormedResponseBodyException("JSON in response body was not in the expected format");
    }
    if(array==null)
      throw new BadlyFormedResponseBodyException();
    for(JsonElement jsonElement : array) {
      JsonObject object = jsonElement.getAsJsonObject();
      names.add(object.get("external_user_id").getAsString());
    }
    return names;
  }
}
