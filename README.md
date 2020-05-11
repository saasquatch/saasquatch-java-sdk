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

This library aims to abstract away the I/O layer and [Reactive Streams](https://www.reactive-streams.org/) implementations to be implementation agnostic. As of right now, this library depends on [RxJava 3](https://github.com/ReactiveX/RxJava), [Gson](https://github.com/google/gson), and [Apache HttpClient 5](https://hc.apache.org/httpcomponents-client-5.0.x/index.html), but never exposes library-specific interfaces other than Reactive Streams interfaces. **It is recommended that you explicitly import the transitive dependencies if you intend to use them**, since we may upgrade or switch to other I/O or Reactive Streams libraries in the future.

## Using the SDK

The entry point of the SDK is `SaaSquatchClient`. To create a `SaaSquatchClient` for your tenant with default options, use:

```java
SaaSquatchClient.createForTenant("yourTenantAlias");
```

If you are in a multi-tenant environment, you can create a tenant-agnostic `SaaSquatchClient` like this:

```java
SaaSquatchClient.create(ClientOptions.newBuilder().build());
```

The code above will create a `SaaSquatchClient` without a default `tenantAlias`, in which case you'll need to pass in a `tenantAlias` via `RequestOptions` for every request you make.

You can also use more advanced options like this:

```java
SaaSquatchClient.create(ClientOptions.newBuilder()
    .setTenantAlias("yourTenantAlias")
    /*
     * This sets the default tenant API key. Note that this option makes more sense
     * if you are using this SDK on the server side. Use this with caution if you are
     * building an Android app.
     */
    .setAuthMethod(AuthMethod.ofApiKey("yourApiKey"))
    .setRequestTimeout(5, TimeUnit.SECONDS)
    // etc.
    .build());
```

It is recommended that you keep a singleton `SaaSquatchClient` for all your requests. Do not create a new `SaaSquatchClient` for every request. `SaaSquatchClient` implements `Closeable`, and it's a good idea to call `close()` to release resources when you are done with it.

Every API method in `SaaSquatchClient` takes a `RequestOptions`, where you can specify your `tenantAlias` override, authentication method override, etc. The per-method `RequestOptions` always takes precedence over the client-level `ClientOptions`.

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

## Unstable APIs

Anything marked with the `@Beta` or `@Internal` annotations, as well as anything under the package `com.saasquatch.sdk.internal`, are either experimental or considered private API, and can be modified or removed without warning.

## Development

This project uses a simple Maven build. Compile wilth `mvn compile` and run tests with `mvn test`.

To run integration tests, you'll need a SaaSquatch account, and run:

```bash
mvn test -D"com.saasquatch.sdk.test.appDomain"="REPLACEME" -D"com.saasquatch.sdk.test.tenantAlias"="REPLACEME" -D"com.saasquatch.sdk.test.apiKey"="REPLACEME"
```

## LICENSE

```
Copyright 2019 ReferralSaaSquatch.com Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
