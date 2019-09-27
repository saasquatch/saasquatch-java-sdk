package com.saasquatch.sdk;

import java.io.IOException;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

class InternalUtils {

  public static String buildUserAgent() {
    final String javaVersion = System.getProperty("java.version", "Unknown");
    final String osName = System.getProperty("os.name", "");
    final String osVersion = System.getProperty("os.version", "");
    final String osStr = (osName + ' ' + osVersion).trim();
    return String.format("SaaSquatch SDK; %s; %s", "Java " + javaVersion,
        osStr.isEmpty() ? "Unknown OS" : osStr);
  }

  public static Flowable<Response> executeRequest(OkHttpClient okHttpClient, Request request) {
    return Single.<Response>create(emitter -> {
      okHttpClient.newCall(request).enqueue(new okhttp3.Callback() {
        @Override
        public void onFailure(Call call, IOException ex) {
          emitter.onError(ex);
        }

        @Override
        public void onResponse(Call call, Response resp) throws IOException {
          emitter.onSuccess(resp);
        }
      });
    }).toFlowable();
  }

}
