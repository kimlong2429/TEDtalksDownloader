package com.longvu.ted.actions.api;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;

public interface AsyncHttpClientFactory {
	public AsyncHttpClient create();
	public AsyncHttpClient create(AsyncHttpClientConfig config);
}
