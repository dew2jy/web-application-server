package util;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;

import common.HttpMethod;

public class RequestLineTest {

	@Test
	public void create_method() {
		RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
		assertEquals(HttpMethod.GET, line.getMethod());
		assertEquals("/index.html", line.getPath());
		
		line = new RequestLine("POST /index.html HTTP/1.1");
		assertEquals(HttpMethod.POST, line.getMethod());
	}

	@Test
	public void create_path_and_params() {
		RequestLine line = new RequestLine(
				"GET /user/create?userId=javajigi&password=pass HTTP/1.1");
		assertEquals(HttpMethod.GET, line.getMethod());
		assertEquals("/user/create", line.getPath());
		Map<String,String> params = line.getParams();
		assertEquals(2, params.size());
		assertEquals("javajigi", params.get("userId"));
	}
}
