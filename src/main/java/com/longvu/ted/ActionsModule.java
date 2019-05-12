package com.longvu.ted;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.actions.api.IExportAction;
import com.longvu.ted.actions.api.IMappingAction;
import com.longvu.ted.actions.impl.DownloadActionImpl;
import com.longvu.ted.actions.impl.MappingActionImpl;
import com.longvu.ted.export.pdf.PdfExportAction;

public class ActionsModule extends AbstractModule {
	private static Logger logger = LoggerFactory.getLogger(ActionsModule.class);
	private static final String FONTS_DIR = "fonts";
	
	@Override
	protected void configure() {
		bind(ExecutorService.class).toInstance(Executors.newFixedThreadPool(5));

		// bind actions
		bind(IDownloadAction.class).to(DownloadActionImpl.class);
		bind(IMappingAction.class).to(MappingActionImpl.class);
		bind(new TypeLiteral<IExportAction<Document>>(){}).to(PdfExportAction.class);

		// bind font
		URL url = Thread.currentThread().getContextClassLoader().getResource(FONTS_DIR);
		try {
			String dir = new File(url.toURI()).getAbsolutePath();
			int fonts = FontFactory.registerDirectory(dir, true);
			logger.debug("Registered {} fonts from {}", fonts, dir);
		} catch (URISyntaxException e) {
		}
	}
	
}
