package controller;

import db.DataBase;
import model.User;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class CreateUserController extends AbstractController{

	@Override
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
		User user = User.builder()
				.userId(request.getParameter("userId"))
				.email(request.getParameter("email"))
				.password(request.getParameter("password"))
				.name(request.getParameter("name"))
				.build();
		
		DataBase.addUser(user);
		
		response.sendRedirect("/index.html");
	}

}
