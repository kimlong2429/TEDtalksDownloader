package com.longvu.ted.actions.api;

import java.util.concurrent.CompletableFuture;

import com.longvu.ted.TEDtalk;
import com.longvu.ted.model.TranscriptResponse;

public interface IDownloadAction {
	public CompletableFuture<TranscriptResponse> download(TEDtalk talk, String language);
}
