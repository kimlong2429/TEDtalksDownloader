package com.longvu.ted.response.handler;

import java.nio.charset.Charset;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longvu.ted.model.Language;
import com.longvu.ted.model.Transcript;

public class TranscriptResponseHandler extends AsyncCompletionHandler<Transcript>{
	private static Logger logger = LoggerFactory.getLogger(TranscriptResponseHandler.class);
	
	private Language language;
	
	public TranscriptResponseHandler(Language language) {
		this.language = language;
	}

	@Override
	public Transcript onCompleted(Response response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String text = response.getResponseBody(Charset.forName("utf-8")).replaceAll("\\\\n", " ");
		logger.trace("Content {} transcipt: {}", language, text);
		return mapper.readValue(text, Transcript.class);
	}
}
