import java.io.IOException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.Response;

import com.longvu.ted.utils.TEDutils;

public class MainTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException, Exception {
		AsyncHttpClientConfig config = TEDutils.generateDefaultTEDAsyncHttpClientConfig();
		AsyncHttpClient client = new DefaultAsyncHttpClient(config);
		client.prepareGet("https://www.ted.com/talks/alex_gendler_how_tsunamis_work")
		.execute(new AsyncCompletionHandler<String>() {

			@Override
			public String onCompleted(Response response) throws Exception {
				String content = response.getResponseBody(Charset.forName("utf-8"));
				System.out.println(content);
				// find talk Id
				Matcher m = Pattern.compile("\"current_talk\":\"(\\d+)").matcher(content);
				if (m.find()) {
					System.out.println("Talk Id: " + m.group(1));
				} else {
					System.err.println("Not found talk id");
				}
				
				// find talk title
				m = Pattern.compile("<title>(.*?)</title>").matcher(content);
				if (m.find()) {
					System.out.println("Title: " + m.group(1));
				} else {
					System.err.println("Not found talk id");
				}
				
				// find talk description
				m = Pattern.compile("<meta\\s+name=\"description\"\\s+content=\"(.*?)\"").matcher(content);
				while (m.find()) {
					System.out.println("Description: " + StringEscapeUtils.unescapeHtml4(m.group(1)));
				}
				
				return null;
			}
		})
		.get();
		
		client.close();
	}
}
