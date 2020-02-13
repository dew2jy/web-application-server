package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class LoginController extends AbstractController{

	@Override
	public void doPost(HttpRequest request, HttpResponse response) throws Exception {
		User user = DataBase.findUserById(request.getParameter("userId"));
		String url = "";
		
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
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		
	}

}
