package util;

import java.util.HashMap;
import java.util.Map;

import common.HttpMethod;

public class RequestLine {
	private HttpMethod method;
	private String path;
	private Map<String, String> params = new HashMap<>();
	
	public RequestLine(String line) {
		String[] tokens = line.split(" ");
		
		if(tokens.length != 3) {
			throw new IllegalArgumentException(line + "이 형식에 맞지 않습니다.");
		}
		
		this.method = HttpMethod.valueOf(tokens[0]);
		
		if(method.isPost()) {
			this.path = getDefaultUrl(tokens);
			return;
		}
		
		int index = tokens[1].indexOf("?");
		if(index == -1) {
			this.path = getDefaultUrl(tokens);
		} else {
			this.path = tokens[1].substring(0, index);
			this.params = HttpRequestUtils.parseQueryString(tokens[1].substring(index+1));
		}
	}
	
	private String getDefaultUrl(String[] tokens) {
        String url = tokens[1];
        if (url.equals("/")) {
            url = "/index.html";
        }
        return url;
    }

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParams() {
		return params;
	}
}
