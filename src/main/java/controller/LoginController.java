package controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class LoginController extends AbstractController {
	
	private static final Logger log = 
			LoggerFactory.getLogger(LoginController.class);

	@Override
	public void doPost(HttpRequest request, HttpResponse response) {
		User user = DataBase.findUserById(request.getParameter("userId"));
		if (user != null) {
			if (user.login(request.getParameter("password"))) {
				response.addHeader("Set-Cookie", "logined=true; Path=/");
				response.sendRedirect("/index.html");
			} else {
				response.sendRedirect("/user/login_failed.html");        				
			}
		} else {        			
			response.sendRedirect("/user/login_failed.html");
		}

	}

}
