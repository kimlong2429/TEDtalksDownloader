package com.longvu.ted;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.asynchttpclient.AsyncHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.longvu.ted.actions.api.AsyncHttpClientFactory;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.actions.api.IGenerateAction;
import com.longvu.ted.actions.api.ISaveAction;
import com.longvu.ted.actions.impl.DownloadActionImpl;
import com.longvu.ted.actions.impl.SaveActionImpl;
import com.longvu.ted.actions.impl.XlsxGenerateActionImpl;
import com.longvu.ted.model.TranscriptResponse;
import com.longvu.ted.response.handler.TranscriptResponseHandler;

public class ActionsModule extends AbstractModule {
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(5));
		
		// bind actions
		bind(IDownloadAction.class).to(DownloadActionImpl.class);
		bind(IGenerateAction.class).to(XlsxGenerateActionImpl.class);
		bind(ISaveAction.class).to(SaveActionImpl.class);
		
		// bind http client and handler
		install(new FactoryModuleBuilder()
				.implement(AsyncHttpClient.class, DefaultAsyncHttpClient.class)
				.build(AsyncHttpClientFactory.class));
		bind(new TypeLiteral<AsyncHandler<TranscriptResponse>>(){}).to(TranscriptResponseHandler.class);
	}
	
}
