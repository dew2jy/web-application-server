package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {
	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);
	
	private Map<String, String> headers;
	private OutputStream out;
	private byte[] body;
	private int lengthOfBodyContent;
	
	public HttpResponse(OutputStream out) {
		this.out = out;
		headers = new HashMap<>();
	}
	
	public void forward(String url) throws IOException {
		if(this.body == null) {
			setBody(Files.readAllBytes(new File("./webapp" + url).toPath()));
		}
		DataOutputStream dos = new DataOutputStream(out);
    	if(url.endsWith(".css")) {
    		responseCssResource(dos);
    	} else {
    		responseResource(dos);
    	}
	}
	
	public void sendRedirect(String url) throws Exception {
		if(this.body == null) {
			setBody(Files.readAllBytes(new File("./webapp" + url).toPath()));
		}
		DataOutputStream dos = new DataOutputStream(out);
		responseHeader(dos, url);
		if(this.body != null) {
			responseBody(dos);
		}
	}
	
	public Map<String, String> getHeaders() {
		return headers;
	}

	public OutputStream getOut() {
		return out;
	}

	public void setBody(byte[] body) {
		this.body = body;
		this.lengthOfBodyContent = body.length;
	}
	
	public byte[] getBody() {
		return body;
	}
	
	public void addHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
	}
	
	private void responseResource(DataOutputStream dos) throws IOException {
        response200Header(dos);
        responseBody(dos);
    }

    private void responseCssResource(DataOutputStream dos) throws IOException {
        response200CssHeader(dos);
        responseBody(dos);
    }

	private void response200Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200CssHeader(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseHeader(DataOutputStream dos, String url) throws Exception{
    	dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
    	headers.forEach((headerName, headerValue) -> {
    		try {
				dos.writeBytes(String.format("%s: %s \r\n", headerName, headerValue));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
    	dos.writeBytes(String.format("Location: %s \r\n", url));
    	dos.writeBytes("\r\n");
    }

    private void responseBody(DataOutputStream dos) {
        try {
            dos.write(body, 0, body.length);
            dos.writeBytes("\r\n");
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
}
