package com.longvu.ted.model;

import java.util.ArrayList;
import java.util.List;

public class Cue implements Comparable<Cue>{
	private Long time;
	private String text;
	private List<Cue> translates;
	
	public Long getTime() {
		return time;
	}
	
	public void setTime(Long time) {
		this.time = time;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}

	public List<Cue> getTranslates() {
		return translates;
	}

	public void setTranslates(List<Cue> translates) {
		this.translates = translates;
	}
	
	public void addTranslate(Cue translate) {
		if (translates == null) {
			translates = new ArrayList<>();
		}
		
		translates.add(translate);
	}

	@Override
	public int compareTo(Cue o) {
		return Long.compare(time, o.getTime());
	}

	@Override
	public String toString() {
		return "Cue [time=" + time + ", text=" + text + "]";
	}
	
}
