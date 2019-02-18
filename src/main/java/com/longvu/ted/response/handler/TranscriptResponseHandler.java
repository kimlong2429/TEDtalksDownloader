package com.longvu.ted.response.handler;

import java.nio.charset.Charset;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.Response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longvu.ted.model.TranscriptResponse;

public class TranscriptResponseHandler extends AsyncCompletionHandler<TranscriptResponse>{
	
	@Override
	public TranscriptResponse onCompleted(Response response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String text = response.getResponseBody(Charset.forName("utf-8")).replaceAll("\\s*\\\\n", " ");
		return mapper.readValue(text, TranscriptResponse.class);
	}
}
