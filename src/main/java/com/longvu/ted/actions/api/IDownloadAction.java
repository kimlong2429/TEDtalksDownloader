package com.longvu.ted.actions.api;

import com.longvu.ted.model.Language;
import com.longvu.ted.model.Transcript;

public interface IDownloadAction {
	public Transcript download(Language language);
}
