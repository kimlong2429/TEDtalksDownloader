package com.longvu.ted.actions.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.poi.ss.usermodel.Workbook;

import com.google.inject.Inject;
import com.longvu.ted.TEDtalk;
import com.longvu.ted.actions.api.ISaveAction;

public class SaveActionImpl implements ISaveAction {
	private static final String FILE_NAME_FORMAT = "%s [ %s ].%s";

	private ExecutorService executor;
	
	@Inject
	public SaveActionImpl(ExecutorService executor) {
		this.executor = executor;
	}
	
	@Override
	public CompletableFuture<Void> save(TEDtalk talk, Object obj) {
		return CompletableFuture.runAsync(() ->{
			if (obj instanceof Workbook) {
				String meta = talk.getTitle().replaceFirst(":", " ][").replaceFirst("\\|", "][");
				saveWorkbook((Workbook) obj, String.format(FILE_NAME_FORMAT, talk.getTalkId(), meta, "xlsx"));
			}
		}, executor);
	}
	
	public void saveWorkbook(Workbook wb, String filename) {
		try (FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir")+ "\\" + filename)) {
			wb.write(fileOut);
			System.out.printf("Saved file: %s\n", filename);
		} catch (IOException e) {
			System.err.printf("Failed to save %s", filename);
			e.printStackTrace(System.err);
		}
	}
}
