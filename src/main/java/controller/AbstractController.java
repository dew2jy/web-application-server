package controller;

import common.HttpMethod;
import webserver.HttpRequest;
import webserver.HttpResponse;

public abstract class AbstractController implements Controller{
	
	@Override
	public void service(HttpRequest request, HttpResponse response) throws Exception {
		HttpMethod method = request.getMethod();
		if(method.isPost()) {
			doPost(request, response);
		}
		
		if(!method.isPost()) {
			doGet(request, response);
		}
	}
	
	protected void doPost(HttpRequest request, HttpResponse response) throws Exception {};
	protected void doGet(HttpRequest request, HttpResponse response) throws Exception {};
}
