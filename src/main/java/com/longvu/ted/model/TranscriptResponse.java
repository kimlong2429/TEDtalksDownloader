package com.longvu.ted.model;

import java.util.List;
import java.util.stream.Collectors;

import com.longvu.ted.model.Paragraph.StandardizedParagraph;

public class TranscriptResponse {
	public class StandardizedTranscript {
		private List<StandardizedParagraph> paragraphs;

		public StandardizedTranscript() {
		}
		
		public StandardizedTranscript(List<StandardizedParagraph> paragraphs) {
			this.paragraphs = paragraphs;
		}

		public List<StandardizedParagraph> getParagraphs() {
			return paragraphs;
		}

	}
	
	private List<Paragraph> paragraphs;

	public List<Paragraph> getParagraphs() {
		return paragraphs;
	}

	public void setParagraphs(List<Paragraph> paragraphs) {
		this.paragraphs = paragraphs;
	}
	
	public StandardizedTranscript standardize() {
		if (paragraphs != null) {
			List<StandardizedParagraph> list = paragraphs.stream().map(p -> p.standardize()).collect(Collectors.toList());
			return new StandardizedTranscript(list);
		}
		
		return new StandardizedTranscript();
	}
}
