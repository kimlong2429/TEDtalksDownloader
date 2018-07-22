package com.longvu.ted.actions.api;

import java.util.concurrent.CompletableFuture;

import com.longvu.ted.TEDtalk;

public interface ISaveAction {
	public CompletableFuture<Void> save(TEDtalk talk, Object obj);
}
