package com.longvu.ted.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

public class TEDutils {
	private static final String DOWNLOAD_TRANSCRIPT_URL = "https://www.ted.com/talks/%s/transcript.json?language=%s";
	private static final String TALK_ID_PATTERN = "\"talk_id\":(\\d+)";
	private static final String TALK_TITLE_PATTERN = "<title>(.*?)</title>";
	private static final String TALK_DESCRIPTION_PATTERN = "<meta\\s+name=\"description\"\\s+content=\"(.*?)\"";
	
	public static String makeDownloadTranscriptUrl(String talkId, String language) {
		return String.format(DOWNLOAD_TRANSCRIPT_URL, talkId, language);
	}
	
	public static String parseTalkId(String content) {
		Matcher m = Pattern.compile(TALK_ID_PATTERN).matcher(content);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	public static String parseTalkTitle(String content) {
		Matcher m = Pattern.compile(TALK_TITLE_PATTERN).matcher(content);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}
	
	public static String parseTalkDescription(String content) {
		Matcher m = Pattern.compile(TALK_DESCRIPTION_PATTERN).matcher(content);
		if (m.find()) {
			return StringEscapeUtils.unescapeHtml4(m.group(1));
		}
		return null;
	}
}
