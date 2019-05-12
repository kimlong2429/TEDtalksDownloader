package com.longvu.ted.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transcript {
	private List<Paragraph> paragraphs;

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}
	
	public void addParagraph(Paragraph p) {
		if (paragraphs == null) {
			paragraphs = new ArrayList<>();
		}
		
		paragraphs.add(p);
	}
}
