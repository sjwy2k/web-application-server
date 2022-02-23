package webserver;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Controller;
import controller.CreateUserController;
import controller.ListUserController;
import controller.LoginController;

public class RequestMapping {
	
	private static final Logger log = 
			LoggerFactory.getLogger(RequestMapping.class);

	private static Map<String, Controller> controllers = new HashMap<>();
	
	static {
		controllers.put("/user/create", new CreateUserController());
		controllers.put("/user/login", new LoginController());
		controllers.put("/user/list", new ListUserController());
	}
	
	public static Controller getController(String requestUrl) {
		return controllers.get(requestUrl);
	}
}
