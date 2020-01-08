package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        String line = null;
        String url = null;
        String requestBody = null;
        int contentLength = 0;
        int responseCode = 200;
        String cookie = "";

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()
        		; BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	int lineCount = 0;
        	while((line=reader.readLine()) != null && !"".equals(line)) {
        		//header
        		if(lineCount == 0) {
        			String[] tokens = line.split(" ");
        			
        			url = tokens[1];
        		} 
        		
        		if(line.startsWith("Content-Length")) {
        			String[] tokens = line.split(" ");
        			
        			contentLength = Integer.parseInt(tokens[1]);
        		}
        		
        		lineCount++;
        	}
        	
        	requestBody = IOUtils.readData(reader, contentLength);
        	
        	if("/user/create".equals(url)) {
        		User user = null;
        		if(requestBody != null) {
        			Map<String, String> paramMaps = HttpRequestUtils.parseQueryString(requestBody);
        			user = new User(paramMaps.get("userId"), paramMaps.get("password"), paramMaps.get("name"), paramMaps.get("email"));
        		}
        		DataBase.addUser(user);
        		
        		responseCode = 302;
        		url = "/index.html";
        	}
        	
        	if("/user/login".equals(url)) {
        		if(requestBody != null) {
        			Map<String, String> paramMaps = HttpRequestUtils.parseQueryString(requestBody);
        			User user = DataBase.findUserById(paramMaps.get("userId"));
        			
        			if(user!=null && user.getPassword().equals(paramMaps.get("password"))) {
        				//login success
        				cookie = "logined=true";
        				url = "/index.html";
        			} else {
        				//login fail
        				cookie = "logined=false";
        				url = "/user/login_failed.html";
        			}
        		}
        	}
        	
        	byte[] body = Files.readAllBytes(new File("./webapp" + url).toPath());
        	
            DataOutputStream dos = new DataOutputStream(out);
            if(responseCode == 302) {
            	response302Header(dos);
            } else {
            	response200Header(dos, body.length, cookie);
            }
            responseBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String cookie) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            dos.writeBytes("Set-Cookie: " + cookie + "\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    
    private void response302Header(DataOutputStream dos) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: /index.html\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
