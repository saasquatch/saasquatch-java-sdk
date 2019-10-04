package com.saasquatch.sdk;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

enum InternalThreadFactory implements ThreadFactory {

  INSTANCE;

  private final String baseName = "SaaSquatch-SDK-Dispatcher";
  private final ThreadGroup threadGroup = new ThreadGroup(baseName);
  private final AtomicLong idx = new AtomicLong();

  @Override
  public Thread newThread(Runnable r) {
    final Thread thread = new Thread(threadGroup, r, baseName + '-' + idx.getAndIncrement());
    if (!thread.isDaemon()) {
      thread.setDaemon(true);
    }
    if (thread.getPriority() != Thread.NORM_PRIORITY) {
      thread.setPriority(Thread.NORM_PRIORITY);
    }
    return thread;
  }

}
