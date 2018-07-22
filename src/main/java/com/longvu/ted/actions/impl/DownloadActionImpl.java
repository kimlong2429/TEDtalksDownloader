package com.longvu.ted.actions.impl;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import com.google.inject.Inject;
import com.longvu.ted.TEDtalk;
import com.longvu.ted.actions.api.AsyncHttpClientFactory;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.model.TranscriptResponse;
import com.longvu.ted.response.handler.TranscriptResponseHandler;
import com.longvu.ted.utils.TEDutils;
import com.ning.http.client.AsyncHttpClient;

public class DownloadActionImpl implements IDownloadAction {

	private AsyncHttpClientFactory factory;
	private ExecutorService executor;
	
	@Inject
	public DownloadActionImpl(AsyncHttpClientFactory factory, ExecutorService executor) {
		this.factory = factory;
		this.executor = executor;
	}

	@Override
	public CompletableFuture<TranscriptResponse> download(TEDtalk talk, String language) {
		return CompletableFuture.supplyAsync(() -> {
			try (AsyncHttpClient client = factory.create()) {
				return client.prepareGet(TEDutils.makeDownloadTranscriptUrl(talk.getTalkId(), language))
						.execute(new TranscriptResponseHandler())
						.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}, executor);
	}

}
