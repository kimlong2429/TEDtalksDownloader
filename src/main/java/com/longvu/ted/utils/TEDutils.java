package com.longvu.ted.utils;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.text.StringEscapeUtils;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.netty.ssl.JsseSslEngineFactory;

public class TEDutils {
	private static final String DOWNLOAD_TRANSCRIPT_URL = "https://www.ted.com/talks/%s/transcript.json?language=%s";
	private static final String TALK_ID_PATTERN = "\"current_talk\":\"(\\d+)";
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
	
	private static SSLContext createSSLContext() throws Exception {
        X509TrustManager tm = new X509TrustManager() {

        	 @Override
             public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
             }

             @Override
             public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
             }

             @Override
             public java.security.cert.X509Certificate[] getAcceptedIssuers() {
               return new X509Certificate[0];
             }
        };

        SSLContext ctx = SSLContext.getInstance("TLS");
        ctx.init(null, new TrustManager[] { tm }, null);
        return ctx;
    }
	
	public static AsyncHttpClientConfig generateDefaultTEDAsyncHttpClientConfig() throws Exception {
		SSLContext sslContext = createSSLContext();
		JsseSslEngineFactory sslEngineFactory = new JsseSslEngineFactory(sslContext);
		AsyncHttpClientConfig config = new DefaultAsyncHttpClientConfig.Builder()
				.setSslEngineFactory(sslEngineFactory)
				.build();
		
		return config;
	}
}
