package com.longvu.ted.actions.api;

import java.util.concurrent.CompletableFuture;

import com.longvu.ted.TEDtalk;
import com.longvu.ted.utils.TranscriptDocument;

public interface IGenerateAction {
	public CompletableFuture<Object> generate(TEDtalk talk, TranscriptDocument transcript);
}
