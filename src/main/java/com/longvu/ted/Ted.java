package com.longvu.ted;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Ted {
	private static Logger logger = LoggerFactory.getLogger(Ted.class);
	private static String LOGGER_PATH = "/logback.xml";
	
	public static void main(String[] args) {
		System.setProperty("logback.configurationFile", LOGGER_PATH);
		
		Injector injector = Guice.createInjector(
				new ArgumentsModule(args), 
				new ActionsModule());
		
		TEDtalk talk = injector.getInstance(TEDtalk.class);
		talk.download()
		.whenComplete((r, t) -> {
			if (t == null) {
				logger.info("DONE.");
			} else {
				logger.error("Failed to download [{}] ", talk.getTalkUrl(), t);
			}
			
			ExecutorService executor = injector.getInstance(ExecutorService.class);
			if (executor != null) {
				executor.shutdown();
			}
		})
		.join();
	}
}
