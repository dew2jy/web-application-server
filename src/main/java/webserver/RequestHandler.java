package webserver;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.AbstractController;
import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

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
        Map<String, Controller> controllers = new HashMap<>();

        controllers.put("/user/create", new CreateUserController());
        controllers.put("/user/login", new LoginController());
        controllers.put("/user/list", new ListUserController());
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
        	HttpRequest request = new HttpRequest(in);
        	url = request.getPath();

        	HttpResponse response = new HttpResponse(out);
        	
        	if(controllers.containsKey(url)) {
        		AbstractController acontroller = (AbstractController) controllers.get(url);
            	acontroller.service(request, response);
        	} else {
        		response.forward(url);
        	}
        	
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    

}
