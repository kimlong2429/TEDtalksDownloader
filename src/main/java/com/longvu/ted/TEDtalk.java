package com.longvu.ted;

import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.longvu.ted.actions.api.AsyncHttpClientFactory;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.actions.api.IGenerateAction;
import com.longvu.ted.actions.api.ISaveAction;
import com.longvu.ted.utils.TEDutils;
import com.longvu.ted.utils.TranscriptDocument;

public class TEDtalk {
	private static final String VI = "vi";
	private static final String EN = "en";
	
	private String talkUrl;

	@Inject
	private IDownloadAction downloadAction;
	@Inject
	private IGenerateAction generateAction;
	@Inject
	private ISaveAction saveAction;
	@Inject
	private AsyncHttpClientFactory factory;
	@Inject
	private ExecutorService executor;
	
	private String talkId;
	private String title;
	private String description;

	@Inject
	public TEDtalk(@Named("talkUrl") String talkUrl) {
		this.talkUrl = talkUrl;
	}
	
	public CompletableFuture<Void> download() {
		return extractTalkInfor()
		.thenCompose(r ->
				downloadAction.download(TEDtalk.this, EN).thenCombineAsync(
					downloadAction.download(TEDtalk.this, VI), 
					TranscriptDocument::new))
		.thenCompose(r ->
				generateAction.generate(TEDtalk.this, r))
		.thenCompose(r ->
				saveAction.save(TEDtalk.this, r));
	}
	
	private CompletableFuture<Void> extractTalkInfor() {
		return CompletableFuture.supplyAsync(() -> {
			AsyncHttpClient client = null;
			try {
				client = new DefaultAsyncHttpClient(TEDutils.generateDefaultTEDAsyncHttpClientConfig());
				
				return client.prepareGet(talkUrl)
						.execute(new AsyncCompletionHandler<Void>() {

							@Override
							public Void onCompleted(Response response) throws Exception {
								String content = response.getResponseBody(Charset.forName("utf-8"));
								TEDtalk.this.talkId = TEDutils.parseTalkId(content);
								TEDtalk.this.title = TEDutils.parseTalkTitle(content);
								TEDtalk.this.description = TEDutils.parseTalkDescription(content);
								return null;
							}
						})
						.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (client != null) {
					try {
						client.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, executor);
	}
	
	public String getTalkUrl() {
		return talkUrl;
	}

	public String getTalkId() {
		return talkId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	

	@Override
	public String toString() {
		return "TEDtalk [talkUrl=" + talkUrl + ", talkId=" + talkId + ", title=" + title + ", description="
				+ description + "]";
	}

	
	
}
