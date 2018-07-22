package com.longvu.ted.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.longvu.ted.model.Paragraph.StandardizedParagraph;
import com.longvu.ted.model.TranscriptResponse;
import com.longvu.ted.model.TranscriptResponse.StandardizedTranscript;

public class TranscriptDocument {
	private StandardizedTranscript enSubtitle;
	private StandardizedTranscript viSubtitle;

	public TranscriptDocument(TranscriptResponse enScript, TranscriptResponse viScript) {
		this.enSubtitle = enScript.standardize();
		this.viSubtitle = viScript.standardize();
		normalize();
	}

	private TranscriptDocument normalize() {
		StandardizedTranscript script1 = null;
		StandardizedTranscript script2 = null;
		if (enSubtitle.getParagraphs().size() > viSubtitle.getParagraphs().size()) {
			script1 = enSubtitle; 
			script2 = viSubtitle;
		} else {
			script1 = viSubtitle; 
			script2 = enSubtitle;
		}
		
		List<StandardizedParagraph> paragraphs = new ArrayList<>(script2.getParagraphs());
		int index1 = 0;
		for (int i = 0; i < paragraphs.size(); i++) {
			StandardizedParagraph p = paragraphs.get(i);
			long min = Math.abs(p.getTime() - script1.getParagraphs().get(index1).getTime());
			for (int j = index1 + 1; j < script1.getParagraphs().size(); j++) {
				long tmp = Math.abs(p.getTime() - script1.getParagraphs().get(j).getTime());
				if (tmp > min) {
					index1 = j;
					break;
				} else {
					min = tmp;
				}
				script2.getParagraphs().add(j - 1, new StandardizedParagraph());
			}
		}
		
		Iterator<StandardizedParagraph> iter1 = script1.getParagraphs().iterator();
		Iterator<StandardizedParagraph> iter2 = script2.getParagraphs().iterator();
		StandardizedParagraph item = null;
		while (iter1.hasNext()) {
			StandardizedParagraph next1 = iter1.next();
			StandardizedParagraph next2 = iter2.next();
			if (item == null || !next2.getParagraph().isEmpty()) {
				item = next1;
			} else if (next2.getParagraph().isEmpty()) {
				item.merge(next1);
				iter1.remove();
				iter2.remove();
			}
		}
		
		return this;
	}

	public StandardizedTranscript getEnSubtitle() {
		return enSubtitle;
	}

	public StandardizedTranscript getViSubtitle() {
		return viSubtitle;
	}
}
