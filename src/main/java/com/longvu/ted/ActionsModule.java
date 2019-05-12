package com.longvu.ted;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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

		// register fonts
		URL url = Thread.currentThread().getContextClassLoader().getResource(FONTS_DIR);
		registerFonts(url);
	}
	
	private void registerFonts(URL url) {
		try {
			File f = new File(url.getPath());
			if (!f.exists()) {
				// get current jar
				File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
				
				JarFile jf = new JarFile(jarFile);
				Enumeration<JarEntry> entries = jf.entries();
				
				while (entries.hasMoreElements()) {
					JarEntry element = entries.nextElement();
					if (element.getName().startsWith(FONTS_DIR) && !element.getName().endsWith("/")) {
						try {
							FontFactory.register(element.getName());
						} catch (Exception e) {
							logger.error("Failed to register font: {} ", element.getName(), e);
						}
					}
				}
				jf.close();
			} else {
				int fonts = FontFactory.registerDirectory(url.getPath(), true);
				logger.trace("Registered {} fonts from {}", fonts, url.getPath());
			}
		} catch (Exception e) {
			logger.error("Failed to register fonts", e);
		}
	}
}
