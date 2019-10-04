package com.saasquatch.sdk;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

class InternalThreadFactory implements ThreadFactory {

  private final AtomicLong idx = new AtomicLong();
  private final String baseName;
  private final ThreadGroup threadGroup;

  public InternalThreadFactory(String clientId) {
    this.baseName = "SaaSquatch-SDK-" + clientId;
    this.threadGroup = new ThreadGroup(baseName);
  }

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
