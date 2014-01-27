service-versioning-demo
=======================

Welcome to the `service-versioning-demo` application, a very simple and contrived example of how to version RPC calls over AMQP.

### Code Organization

The code is separated into three major packages under the top level `com.interzonedev.serviceversioningdemo` package:

* `common` - Code common to both client and service.
* `client` - The client side of the RPC calls.  Polls for user input, forms and sends AMQP requests.
* `service` - The service side of the RPC calls.  Listens for AMQP requests and performs the business logic.

Each major package in turn has version specific subpackages (`v1` and `v2`) that allow simultaneous deployment of more than one version of the code.  Each package also has an `all` subpackage for code that is shared across versions in each major package.

The version refers to the major version in the standard semantic versioning scheme of `<major version>.<minor version>.<patch version>`.


### The API

The service basically echoes to `System.out` the set of strings sent by the client.  It will use either the `System.out.print` or `System.out.println` method depending on the properties of the client request.

The difference between version 1 and 2 is that version 2 accepts a boolean indicating whether or not to also print a timestamp.

The version 1 API as implemented on both the client and service side is:

```java
public interface ExampleAPI {
    public void print(String message);
    public void println(String message);
}
```

The version 2 API as implemented on both the client and service side is:

```java
public interface ExampleAPI {
    public void print(String message, boolean timestamp);
    public void println(String message, boolean timestamp);
}
```

The medium of exchange between the client and service is the `Command` value object that encapsulates the method to call on the service and any arguments that should be passed into that method.

### Building and Running

The project is built with Maven and uses the `appassembler-maven-plugin` which produces shell scripts that each run a specific project target (i.e. `main` method on a class).  The `bin` directory contains convenience shell scripts with the correct permissions set to run the scripts produced by `appassembler-maven-plugin` and are:

* `bin/service.sh` - Launches the service with both version 1 and 2 implementations.  Listens for and processes requests.
* `bin/v1_client.sh` - Launches a version 1 specific client.  Polls for command line input and sends version 1 requests.
* `bin/v2_client.sh` - Launches a version 2 specific client.  Polls for command line input and sends version 2 requests.

The shell scripts must be executed in the project base directory as `appassembler-maven-plugin` produces an "exploded" build.  Not all of the required artifacts are contained in the resulting jar (`target/service-versioning-demo-XXX.jar`) but are instead found individually in the local repository created by `appassembler-maven-plugin` (`target/repo`).  If the shell scripts are not run in the base project directory the required artifacts can not be put correctly into the classpath.

For instance, to launch the service execute `./bin/service.sh` in the project base directory.

### Using the Command Line

The service just sits and listens for requests and has no user interaction but both the version 1 and 2 clients require command line input to make requests.

The format of the command line input for the version 1 client is:
`<method> <message part 1> [<message part 2>...]`
where the method is either `print` or `println` and the message parts are arbitrary stings

The format of the command line input for the version 2 client is:
`<method> [<timestamp>] <message part 1> [<message part 2>...]`
where the method is either `print` or `println`, timestamp is either `true` or `false` and the message parts are arbitrary stings

Entering "quit" exits the client for both versions.

### Version Determination

Since both versions of the service are running simultaneously (both the `v1` and `v2` packages are compiled into the service), there needs to be a way to determine which version a particular AMQP request is targeting.

AMQP has several degrees of freedom how the client sends requests and how the service binds to received requests.

A client publishes messages to an exchange with an optional routing key and an optional set of headers (similar to HTTP request headers)  A service listens for messages on a queue that is bound to a particular combination of an exchange, routing key and headers.

In `service-versioning-demo`, a client will make use of a header to determine which version of a service it wishes to call.  A service will declare a single queue and look for a particular header value for each version it supports.  Both the client and service declare and bind to the same exchange and use the same routing key.

The client declares a direct exchange:

```java
channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");
```

Then sends messages with a version specific header:

```java
Map<String, Object> headers = new HashMap<String, Object>();
headers.put(ExampleAMQP.VERSION_HEADER_NAME, getVersion());
BasicProperties messageProperties = new BasicProperties.Builder().headers(headers).build();
channel.basicPublish(ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.ROUTING_KEY, messageProperties, messageBody);
```

The service declares a direct exchange and binds to it:

```java
channel.exchangeDeclare(ExampleAMQP.EXCHANGE_NAME, "direct");
channel.queueDeclare(ExampleAMQP.QUEUE_NAME, false, false, true, null);
channel.queueBind(ExampleAMQP.QUEUE_NAME, ExampleAMQP.EXCHANGE_NAME, ExampleAMQP.ROUTING_KEY);
channel.basicConsume(ExampleAMQP.QUEUE_NAME, true, consumer);
```

Then receives messages and looks for the version header:

```java
Map<String, Object> headers = delivery.getProperties().getHeaders();
String version = headers.get(ExampleAMQP.VERSION_HEADER_NAME).toString();
```

The correct service code is then invoked based on the version found in the header.
