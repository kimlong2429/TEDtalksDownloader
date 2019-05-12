package com.longvu.ted.model;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DuoDocument {
	private static ObjectMapper mapper = new ObjectMapper();
	
	private List<Section> sections;
	
	public DuoDocument(List<Section> sections) {
		this.sections = sections;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}

	@Override
	public String toString() {
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
		}
		
		return super.toString();
	}

	public static class Section {
		private List<Line> lines;

		public Section(List<Line> lines) {
			this.lines = lines;
		}

		public List<Line> getLines() {
			return lines;
		}

		public void setLines(List<Line> lines) {
			this.lines = lines;
		}
		
	}
	
	public static class Line {
		private String source;
		private String translate;
		
		public Line(String source, String translate) {
			this.source = source;
			this.translate = translate;
		}
		
		public String getSource() {
			return source;
		}
		
		public void setSource(String source) {
			this.source = source;
		}
		
		public String getTranslate() {
			return translate;
		}
		
		public void setTranslate(String translate) {
			this.translate = translate;
		}
	}
}
