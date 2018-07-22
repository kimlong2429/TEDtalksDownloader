package com.longvu.ted;

import java.util.concurrent.ExecutorService;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Ted {
	
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(
				new ArgumentsModule(args), 
				new ActionsModule());
		
		TEDtalk talk = injector.getInstance(TEDtalk.class);
		talk.download()
		.whenComplete((r, t) -> {
			if (t == null) {
				ExecutorService executor = injector.getInstance(ExecutorService.class);
				if (executor != null) {
					executor.shutdown();
				}
				System.out.println("DONE.");
			}
		})
		.join();
	}
}
