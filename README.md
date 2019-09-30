# SaaSquatch Java SDK (UNDER CONSTRUCTION!!!)

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![](https://jitpack.io/v/saasquatch/saasquatch-java-sdk.svg)](https://jitpack.io/#saasquatch/saasquatch-java-sdk)

SaaSquatch SDK for Java

## Adding SaaSquatch Java SDK to your project

SaaSquatch Java SDK is hosted on JitPack.

Add JitPack repository:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
  <groupId>com.github.saasquatch</groupId>
  <artifactId>saasquatch-java-sdk</artifactId>
  <version>TODO</version>
</dependency>
```

For more information and other built tools, [please refer to the JitPack page](https://jitpack.io/#saasquatch/saasquatch-java-sdk).

SaaSquatch Java SDK works on Java 8+ and Android API level 21+. To use this library on Android, you'll need to configure your project to use Java 8 by adding the following to `build.gradle` (see [Android official docs](https://developer.android.com/studio/write/java8-support) for more information):

```gradle
android {
  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }
  // For Kotlin projects
  kotlinOptions {
    jvmTarget = "1.8"
  }
}
```

This library aims to abstract away the I/O layer and [Reactive Streams](https://www.reactive-streams.org/) implementations to be implementation agnostic. As of right now, this library depends on [RxJava 2](https://github.com/ReactiveX/RxJava) (with plans of upgrading to RxJava 3), [Gson](https://github.com/google/gson), and [OkHttp](https://square.github.io/okhttp/) (with plans of upgrading to Apache HttpClient 5), but never exposes library-specific interfaces other than Reactive Streams interfaces. __It is recommended that you explicitly import the transitive dependencies if you intend to use them__, since we may switch to other I/O or Reactive Streams libraries in the future.

## Using the SDK

The entry point of the SDK is `SaaSquatchClient`. To create a `SaaSquatchClient` with default options, use:

```java
SaaSquatchClient.createForTenant("yourTenantAlias");
```

If you want more advanced options or you are in a multi-tenant environment, you can use:

```java
SaaSquatchClient.create(ClientOptions.newBuilder()
    .setTenantAlias("yourTenantAlias") // This is optional
    .setRequestTimeout(5, TimeUnit.SECONDS)
    // etc.
    .build());
```

If you create a `SaaSquatchClient` without a `tenantAlias`, then you'll need to pass in a `tenantAlias` via `RequestOptions` for every request you make. The `tenantAlias` in `RequestOptions` takes precedence over the one in `SaaSquatchClientOptions`.

It is recommended that you keep a singleton `SaaSquatchClient` for all your requests. Do not create a new `SaaSquatchClient` for every request. `SaaSquatchClient` implements `Closeable`, and it's a good idea to call `close()` to release resources when you are done with it.

`SaaSquatchClient` returns [Reactive Streams](https://www.reactive-streams.org/) interfaces. Assuming you are using RxJava, then a typical API call made with this SDK would look something like this:

```java
final Map<String, Object> userInput = ...;
final String jwt = ...;
final Publisher<MapApiResponse> responsePublisher =
    saasquatchClient.userUpsert(userInput,
        RequestOptions.newBuilder().setAuthMethod(AuthMethod.ofJwt(jwt)).build());
Flowable.fromPublisher(responsePublisher)
    .doOnNext(response -> {
      System.out.printf("Status[%d] received\n", response.getStatusCode());
      if (response.failed()) {
        // Non 2XX received, in which case we should typically get a standard api error
        final ApiError apiError = response.getApiError();
        System.out.println(apiError.getMessage());
      } else {
        // Getting the raw JSON data as a Map and do whatever you want with it
        final Map<String, Object> data = response.getData();
        // Or unmarshal the JSON result to one of the provided model classes
        final User user = response.toModel(User.class);
        System.out.printf("User with accountId[%s] and id[%s] created\n",
            user.getAccountId(), user.getId());
      }
    })
    .doOnError(ex -> {
      System.out.println("Catastrophic failure!!!");
      ex.printStackTrace();
    })
    .subscribe();
```

TODO

## Development

This project uses a simple Maven build. Compile wilth `mvn compile` and run tests with `mvn test`.

To run integration tests, you'll need a SaaSquatch account, and run:
```bash
mvn test -D"com.saasquatch.sdk.test.appDomain"=REPLACEME -D"com.saasquatch.sdk.test.tenantAlias"=REPLACEME -D"com.saasquatch.sdk.test.apiKey"=REPLACEME
```
