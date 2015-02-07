EventHubJavaClient
==================

This project is a Java HTTP client for the EventHub server from the EventHub project (https://github.com/Codecademy/EventHub) developed by Codecademy.

EventHubJavaClient is based on Jersey and has been tested with Java SE6 and above.

To instantiate a client:

// Replace these values with your EventHub server details
int portnumber = 8080;
String url = "http://localhost:" + portNumber;
int readTimeout = 60000;
int connectionTimeout = 60000;

// Then call either
EventHubClient client  = EventHubClient.createDefaultClient(url, connectionTimeout, readTimeout);

// or

Config config = new DefaultClientConfig();
// Call methods on config to configure the client, as per usual Jersey client configuration
// See https://jersey.java.net for further details
EventHubClient client = EventHubClient.createCustomClient(url, connectionTimeout, readTimeout, config);


