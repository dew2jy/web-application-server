package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import common.HttpMethod;
import util.HttpRequestUtils;
import util.IOUtils;
import util.RequestLine;

public class HttpRequest {
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

	private Map<String, String> headers;
	private Map<String, String> params;
	private RequestLine requestLine;

	public HttpRequest(InputStream inputStream) {
		headers = new HashMap<>();

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line = reader.readLine();

			if (line == null) {
				return;
			}

			log.debug("******* Request Header *******");
			log.debug(line);

			requestLine = new RequestLine(line);
			
			while ((line = reader.readLine()) != null && !"".equals(line)) {
				log.debug(line);

				addHeaders(line);
			}

			if(getMethod().isPost()) {
				String body = IOUtils.readData(reader, Integer.parseInt(getHeader("Content-Length")));
				this.params = HttpRequestUtils.parseQueryString(body);
			} else {
				this.params = requestLine.getParams();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}
	
	private void addHeaders(String line) {
		String[] tokens = line.split(":", 2);

		this.headers.put(tokens[0], tokens[1].trim());
	}
	
	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getHeader(String fieldName) {
		return headers.get(fieldName);
	}
	
	public String getParameter(String parameterName) {
		return params.get(parameterName);
	}
}
