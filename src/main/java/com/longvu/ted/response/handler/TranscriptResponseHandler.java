package com.longvu.ted.response.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.longvu.ted.model.TranscriptResponse;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.Response;

public class TranscriptResponseHandler extends AsyncCompletionHandler<TranscriptResponse>{
	
	@Override
	public TranscriptResponse onCompleted(Response response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String text = response.getResponseBody("utf-8").replaceAll("\\s*\\\\n", " ");
		return mapper.readValue(text, TranscriptResponse.class);
	}
}
