package com.longvu.ted;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.itextpdf.text.Document;
import com.longvu.ted.actions.api.IDownloadAction;
import com.longvu.ted.actions.api.IExportAction;
import com.longvu.ted.actions.api.IMappingAction;
import com.longvu.ted.model.Language;
import com.longvu.ted.model.Transcript;
import com.longvu.ted.utils.TEDutils;

@Singleton
public class TEDtalk {
	private static Logger logger = LoggerFactory.getLogger(TEDtalk.class);
	
	@Inject
	private IDownloadAction downloadAction;
	@Inject
	private IMappingAction mappingAction;
	@Inject
	private IExportAction<Document> pdfExportAction;
	@Inject
	private ExecutorService executor;

	private String talkUrl;
	
	private String talkId;
	private String title;
	private String author;
	private String description;
	private Map<Language, Transcript> transcripts;
	
	private String fileName;

	@Inject
	public TEDtalk(@Named("talkUrl") String talkUrl) {
		this.talkUrl = talkUrl;
		this.transcripts = new ConcurrentHashMap<>();
	}
	
	public CompletableFuture<Void> download() {
		CompletableFuture<TEDtalk> future = extractTalkInfor();
		
		// download required transcripts
		CompletableFuture<Void> enSubtitleDownload = future
				.thenAcceptAsync(r -> transcripts.put(Language.EN, downloadAction.download(Language.EN)), executor);
		CompletableFuture<Void> viSubtitleDownload = future
				.thenAcceptAsync(r -> transcripts.put(Language.VI, downloadAction.download(Language.VI)), executor);
		
		// map them together
		return CompletableFuture.allOf(enSubtitleDownload, viSubtitleDownload)
				.thenApplyAsync(r -> mappingAction.map(getTranscript(Language.EN), getTranscript(Language.VI)), 
						executor)
				.thenAccept(pdfExportAction::export);
	}
	
	private CompletableFuture<TEDtalk> extractTalkInfor() {
		return CompletableFuture.supplyAsync(() -> {
			AsyncHttpClient client = null;
			try {
				client = new DefaultAsyncHttpClient(TEDutils.generateDefaultTEDAsyncHttpClientConfig());
				
				logger.debug("Getting TED talk informations");
				return client.prepareGet(talkUrl)
						.execute(new AsyncCompletionHandler<TEDtalk>() {

							@Override
							public TEDtalk onCompleted(Response response) throws Exception {
								String content = response.getResponseBody(Charset.forName("utf-8"));
								TEDtalk.this.talkId = TEDutils.parseTalkId(content);
								TEDtalk.this.title = TEDutils.parseTalkTitle(content);
								TEDtalk.this.author = TEDutils.parseTalkAuthor(content);
								TEDtalk.this.description = TEDutils.parseTalkDescription(content);
								
								logger.debug("Parsed TED talk informations: {}", TEDtalk.this);
								return TEDtalk.this;
							}
						})
						.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				if (client != null) {
					try {
						client.close();
					} catch (Exception e) {
						logger.error("Failed to close client ", e);
					}
				}
			}
		}, executor);
	}
	
	public String getTalkUrl() {
		return talkUrl;
	}

	public String getTalkId() {
		return talkId;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}
	
	public Map<Language, Transcript> getAllTranscipts() {
		return transcripts;
	}
	
	public Transcript getTranscript(Language language) {
		return transcripts.get(language);
	}
	
	public void addTranscipt(Language language, Transcript transcript) {
		transcripts.put(language, transcript);
	}

	public String getAuthor() {
		return author;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "TEDtalk [talkId=" + talkId + ", title=" + title + ", author=" + author + ", description=" + description
				+ "]";
	}
	
}
