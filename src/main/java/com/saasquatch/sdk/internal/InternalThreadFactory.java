package com.saasquatch.sdk.internal;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class InternalThreadFactory implements ThreadFactory {

  private final AtomicLong idx = new AtomicLong();
  private final String baseName;

  public InternalThreadFactory(String clientId) {
    this.baseName = "SaaSquatch-SDK-" + clientId;
  }

  @Override
  public Thread newThread(Runnable r) {
    final Thread thread = new Thread(r, baseName + '-' + idx.getAndIncrement());
    if (!thread.isDaemon()) {
      thread.setDaemon(true);
    }
    if (thread.getPriority() != Thread.NORM_PRIORITY) {
      thread.setPriority(Thread.NORM_PRIORITY);
    }
    return thread;
  }

}
