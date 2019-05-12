package com.longvu.ted.actions.api;

import com.longvu.ted.model.DuoDocument;

public interface IExportAction<T> {
	public T export(DuoDocument document);
}
