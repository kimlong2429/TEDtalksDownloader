package com.longvu.ted.actions.impl;

import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.longvu.ted.TEDtalk;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.model.Language;
import com.longvu.ted.model.Transcript;
import com.longvu.ted.response.handler.TranscriptResponseHandler;
import com.longvu.ted.utils.TEDutils;

public class DownloadActionImpl implements IDownloadAction {
	private static Logger logger = LoggerFactory.getLogger(DownloadActionImpl.class);

	@Inject
	private TEDtalk talk;
	
	@Override
	public Transcript download(Language language) {
		AsyncHttpClient client = null;
		Transcript transcript = null;
		try {
			client = new DefaultAsyncHttpClient(TEDutils.generateDefaultTEDAsyncHttpClientConfig());
			
			logger.debug("Downloading {} transcript", language);
			transcript = client.prepareGet(TEDutils.makeDownloadTranscriptUrl(talk.getTalkId(), language.code))
					.execute(new TranscriptResponseHandler(language))
					.get();
			
			return transcript;
		} catch (Exception e) {
			logger.error("Failed to get {} transcript ", language, e);
			throw new RuntimeException(e);
		} finally {
			if (client != null) {
				try {
					client.close();
				} catch (Exception e) {
					logger.error("Failed to close client ", e);
				}
			}
		}
	}

}
