# saasquatch-java-sdk (UNDER CONSTRUCTION!!!)

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

This library aims to abstract away the I/O layer and [Reactive Streams](https://www.reactive-streams.org/) implementations to be implementation agnostic. As of right now, this library depends on [RxJava 2](https://github.com/ReactiveX/RxJava), [Gson](https://github.com/google/gson), and [OkHttp](https://square.github.io/okhttp/), but never exposes library-specific interfaces other than Reactive Streams interfaces. It is recommended that you explicitly import the transient dependencies if you intend to use them, since we may switch to other I/O or Reactive Streams libraries in the future.

## Using the SDK

TODO

## Development

This project uses a simple Maven build. Compile wilth `mvn compile` and run tests with `mvn test`.

To run integration tests, you'll need a SaaSquatch account, and run `mvn test -D"com.saasquatch.sdk.test.appDomain"=REPLACEME -D"com.saasquatch.sdk.test.tenantAlias"=REPLACEME -D"com.saasquatch.sdk.test.apiKey"=REPLACEME`.
