package com.longvu.ted.model;

import java.util.List;

public class Paragraph {
	public static class StandardizedParagraph {
		private Long time;
		private String paragraph;
		
		public StandardizedParagraph() {
			paragraph = "";
		}
		
		public StandardizedParagraph(Long time, String paragraph) {
			this.time = time;
			this.paragraph = paragraph;
		}

		public Long getTime() {
			return time;
		}

		public String getParagraph() {
			return paragraph;
		}

		public void merge(StandardizedParagraph other) {
			paragraph = new StringBuilder(paragraph)
					.append(" ")
					.append(other.getParagraph())
					.toString();
		}
	}
	
	private List<Cue> cues;

	public List<Cue> getCues() {
		return cues;
	}

	public void setCues(List<Cue> cues) {
		this.cues = cues;
	}

	public StandardizedParagraph standardize() {
		if (cues != null && !cues.isEmpty()) {
			Long time = cues.get(0).getTime();
			String paragraph = cues.stream()
					.reduce(new StringBuilder(), (builder, cue) -> builder.append(" ").append(cue.getText()), 
							(b1, b2) -> b1.append(b2))
					.toString().trim();
			
			return new StandardizedParagraph(time, paragraph);
		}
		
		return new StandardizedParagraph();
	}
}
