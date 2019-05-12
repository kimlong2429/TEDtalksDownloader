package com.longvu.ted.model;

import java.util.ArrayList;
import java.util.List;

public class Paragraph {
	private List<Cue> cues;

	public List<Cue> getCues() {
		return cues;
	}

	public void setCues(List<Cue> cues) {
		this.cues = cues;
	}
	
	public void addCue(Cue cue) {
		if (cues == null) {
			cues = new ArrayList<>();
		}
		
		cues.add(cue);
	}
}
