package com.saasquatch.sdk;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

enum InternalThreadFactory implements ThreadFactory {

  INSTANCE;

  private static final ThreadFactory defaultThreadFactory = Executors.defaultThreadFactory();

  private final AtomicLong idx = new AtomicLong();

  @Override
  public Thread newThread(Runnable r) {
    final Thread thread = defaultThreadFactory.newThread(r);
    thread.setDaemon(true);
    thread.setName("SaaSquatch-SDK-Dispatcher-" + idx.getAndIncrement());
    return thread;
  }

}
