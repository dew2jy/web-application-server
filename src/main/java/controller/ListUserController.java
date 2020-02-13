package controller;

import java.io.IOException;
import java.util.Collection;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import webserver.HttpRequest;
import webserver.HttpResponse;

public class ListUserController extends AbstractController{

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		String url = "";
		if(!HttpRequestUtils.isLogin(HttpRequestUtils.parseCookies(request.getHeader("Cookie")))) {
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
	}

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response) {
		
	}

}
