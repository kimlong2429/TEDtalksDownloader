package com.longvu.ted.actions.api;

import com.longvu.ted.model.DuoDocument;
import com.longvu.ted.model.Transcript;

public interface IMappingAction {
	public DuoDocument map(Transcript source, Transcript translated);
}
