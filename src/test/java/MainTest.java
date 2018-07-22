import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;

import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class MainTest {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		AsyncHttpClient client = new AsyncHttpClient();
		client.prepareGet("https://www.ted.com/talks/tim_brown_urges_designers_to_think_big")
		.execute(new AsyncCompletionHandler<String>() {

			@Override
			public String onCompleted(Response response) throws Exception {
				String content = response.getResponseBody("utf-8");
				System.out.println(content);
				// find talk Id
				Matcher m = Pattern.compile("\"talk_id\":(\\d+)").matcher(content);
				if (m.find()) {
					System.out.println("Talk Id: " + m.group(1));
				}
				
				// find talk title
				m = Pattern.compile("<title>(.*?)</title>").matcher(content);
				if (m.find()) {
					System.out.println("Title: " + m.group(1));
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
		
		// close connection
		client.close();
	}
}
