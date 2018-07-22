package com.longvu.ted.actions.api;

import com.ning.http.client.AsyncHttpClient;

public interface AsyncHttpClientFactory {
	public AsyncHttpClient create();
}
