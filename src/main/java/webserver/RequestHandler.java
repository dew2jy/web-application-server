package webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());
        String url = null;
        Map<String,String> cookies = null;
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	HttpRequest request = new HttpRequest(in);
        	url = request.getPath();

        	cookies = HttpRequestUtils.parseCookies(request.getHeader("Cookie"));
        	boolean logined = isLogin(cookies);
        	
        	HttpResponse response = new HttpResponse(out);
        	
        	if("/user/create".equals(url)) {
        		User user = User.builder()
    					.userId(request.getParameter("userId"))
    					.email(request.getParameter("email"))
    					.password(request.getParameter("password"))
    					.name(request.getParameter("name"))
    					.build();
        		
        		DataBase.addUser(user);
        		
        		response.sendRedirect("/index.html");
        	} else if("/user/login".equals(url)) {
        		User user = DataBase.findUserById(request.getParameter("userId"));
    			
    			if(user!=null && user.getPassword().equals(request.getParameter("password"))) {
    				//login success
    				response.addHeader("Set-Cookie", "logined=true");
    				url = "/index.html";
    				response.sendRedirect(url);
    			} else {
    				//login fail
    				response.addHeader("Set-Cookie", "logined=false");
    				url = "/user/login_failed.html";
    				response.forward(url);
    			}
        	} else if("/user/list".equals(url)) {
        		if(!logined) {
        			url = "/user/login.html";
        			response.forward(url);
        			return;
        		}
        		
        		Collection<User> users = DataBase.findAll();
        		StringBuilder sb = new StringBuilder();
        		sb.append("<table border='1'>");
        		for(User user : users) {
        			sb.append("<tr>");
        			sb.append("<td>"+user.getUserId()+"</td>");
        			sb.append("<td>"+user.getName()+"</td>");
        			sb.append("<td>"+user.getEmail()+"</td>");
        			sb.append("</tr>");
        		}
        		sb.append("</table>");
        		
        		response.setBody(sb.toString().getBytes());
        		response.forward(url);
        	} else {
        		response.forward(url);
        	}
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    
    private boolean isLogin(Map<String, String> cookies) {
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
    }

}
