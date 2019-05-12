package com.longvu.ted.actions.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.longvu.ted.actions.api.IMappingAction;
import com.longvu.ted.model.Cue;
import com.longvu.ted.model.DuoDocument;
import com.longvu.ted.model.DuoDocument.Line;
import com.longvu.ted.model.DuoDocument.Section;
import com.longvu.ted.model.Paragraph;
import com.longvu.ted.model.Transcript;

public class MappingActionImpl implements IMappingAction {
	private static Logger logger = LoggerFactory.getLogger(MappingActionImpl.class);
	private static ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public DuoDocument map(Transcript source, Transcript translated) {
		// mapping each cue from translated transcript to source transcript
		DuoDocument doc = doMapping(translated, source);
		logger.debug("Translated document: {}", doc);
		
		return doc;
	}

	private DuoDocument doMapping(Transcript from, Transcript dest) {
		Transcript result = new Transcript();
		
		// clone to new transcript
		for(Paragraph p: dest.getParagraphs()) {
			Paragraph nP = new Paragraph();
			
			for(Cue c: p.getCues()) {
				Cue nC = new Cue();
				nC.setText(c.getText());
				nC.setTime(c.getTime());
				
				nP.addCue(nC);
			}
			
			result.addParagraph(nP);
		}
		
		// list of cues
		List<Cue> fromCueList = from.getParagraphs().stream()
				.flatMap(p -> p.getCues().stream())
				.collect(Collectors.toList());
		
		List<Cue> destCueList = result.getParagraphs().stream()
				.flatMap(p -> p.getCues().stream())
				.collect(Collectors.toList());
		
		List<Cue> sortedCues = new ArrayList<>();
		
		// merge them together
		int kTemp = 0;
		for (int i = 0; i < fromCueList.size(); i++) { // translated
			Cue iCue = fromCueList.get(i);
			for (int k = kTemp; k < destCueList.size(); k++) { // source
				Cue kCue = destCueList.get(k);
				kTemp = k;
				if (iCue.compareTo(kCue) <= 0) {
					sortedCues.add(iCue);
					
					// find best match
					if (k == 0) {
						kCue.addTranslate(iCue);
					} else {
						long leftDiff = Math.abs(iCue.getTime() - destCueList.get(k-1).getTime());
						long rightDiff = Math.abs(iCue.getTime() - kCue.getTime());
						if (leftDiff < rightDiff) {
							destCueList.get(k-1).addTranslate(iCue);
						} else if (leftDiff > rightDiff) {
							kCue.addTranslate(iCue);
						} else {
							logger.debug("Unknow which cue is match with {}: {}, {}", iCue, destCueList.get(k-1), kCue);
						}
					}
					
					break;
				} else {
					sortedCues.add(kCue);
				}
			}
			
			if (!sortedCues.contains(iCue)) {
				sortedCues.add(iCue);
			}
		}
		
		try {
			String output = mapper.writeValueAsString(sortedCues);
			logger.debug("Sorted cues: {}", output);
		} catch (JsonProcessingException e) {
		}
		
		try {
			String output = mapper.writeValueAsString(destCueList);
			logger.debug("Translated transcript: {}", output);
		} catch (JsonProcessingException e) {
		}
		
		List<Section> sections = result.getParagraphs().stream()
				.map(p -> { // map each paragraph to section
					// map each cue to line
					List<Line> lines = p.getCues().stream().map(c -> {
						String translate = null;
						// concat all translate cues to one string
						if (c.getTranslates() != null) {
							translate = c.getTranslates()
									.stream()
									.reduce(new StringBuilder(), 
											(b, cue) -> b.append(cue.getText()), 
											(b1, b2) -> b1.append(" ").append(b2.toString()))
									.toString().trim();
						}
						
						return new Line(c.getText(), translate);
					}).collect(Collectors.toList());
					
					return new Section(lines);
				})
				.collect(Collectors.toList());
		
		return new DuoDocument(sections);
	}
}
