package com.longvu.ted;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ArgumentsModule extends AbstractModule {
	private static final String USAGE = "Usage: download <talk-url>";
	
	private String talkUrl;
	
	public ArgumentsModule(String[] args) {
		if (args.length < 1 || args.length > 1) {
			System.err.println("WRONG COMMAND!!!");
			System.out.println(USAGE);
			System.exit(0);
		}

		this.talkUrl = args[0];
	}

	@Override
	protected void configure() {
		bind(String.class).annotatedWith(Names.named("talkUrl")).toInstance(talkUrl);
	}
	
}
