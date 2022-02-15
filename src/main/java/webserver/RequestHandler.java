package webserver;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
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
        
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
        	HttpRequest request = new HttpRequest(in);
        	String path = getDefaultPath(request.getPath());
        	
        	if ("/user/create".equals(path)) {
        		User user = new User(
        				request.getParameter("userId"), 
        				request.getParameter("password"), 
        				request.getParameter("name"),
        				request.getParameter("email"));
        	} else if ("/user/login".equals(path)) {
        		User user = DataBase.findUserById(request.getParameter("userId"));
        		log.debug("User : {}", user);
        		if (user == null) {
        			log.debug("로그인 실패 - 회원조회 실패");
        			responseResource(out, "/user/login_failed.html");
        			return;
        		}
        		if (user.getPassword().equals(request.getParameter("password"))) {
        			log.debug("로그인 성공");
        			DataOutputStream dos = new DataOutputStream(out);
        			response302LoginSuccessHeader(dos);
        		} else {
        			log.debug("로그인 실패 - 비밀번호 불일치");
        			responseResource(out, "/user/login_failed.html");
        		}
        	} else if ("/user/list".equals(path)){
        		if (!HttpRequest.isLogin(request.getHeader("Cookie"))) {
        			responseResource(out, "/user/login.html");
        			return;
        		}
        		Collection<User> users = DataBase.findAll();
        		StringBuilder sb = new StringBuilder();
        		sb.append("<table border='1'>");
        		for (User user : users) {
        			sb.append("<tr>");
        			sb.append("<td>" + user.getUserId()+ "</td>");
        			sb.append("<td>" + user.getName()+ "</td>");
        			sb.append("<td>" + user.getEmail()+ "</td>");
        			sb.append("</tr>");
        		}
        		sb.append("</table>");
        		byte[] body = sb.toString().getBytes();
        		DataOutputStream dos = new DataOutputStream(out);
        		response200Header(dos, body.length);
        		responseBody(dos, body);
        	} else if (path.endsWith(".css")) {
        		responseCssResource(out, path);        		
        	} else {
        		responseResource(out, path);
        	}
        	
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseCssResource(OutputStream out, String path) throws IOException {
    	DataOutputStream dos = new DataOutputStream(out);
		byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
		response200CssHeader(dos, body.length);
		responseBody(dos, body);
	}

	private String getDefaultPath(String path) {
    	if (path.equals("/")) {
    		return "/index.html";
    	}
		return path;
	}

	private void response200CssHeader(DataOutputStream dos, int lengthOfBodyContents) {
    	try {
    		dos.writeBytes("HTTP/1.1 200 OK \r\n");
    		dos.writeBytes("Content-Type: text/css\r\n");
    		dos.writeBytes("Content-Length: " + lengthOfBodyContents + "\r\n");
    		dos.writeBytes("\r\n");
    	} catch (IOException e) {
    		log.error(e.getMessage());
    	}
	}

	private void response302LoginSuccessHeader(DataOutputStream dos) {
    	try {
    		dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
    		dos.writeBytes("Set-Cookie: logined=true \r\n");
    		dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseResource(OutputStream out, String path) throws IOException {
    	DataOutputStream dos = new DataOutputStream(out);
    	byte[] body = Files.readAllBytes(new File("./webapp" + path).toPath());
    	response200Header(dos, body.length);
    	responseBody(dos, body);
	}
	
	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
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
	
	@Deprecated
	private boolean isLogin(String cookieValue) {
		Map<String, String> cookies = 
				HttpRequestUtils.parseCookies(cookieValue);
		String value = cookies.get("logined");
		if (value == null) {
			return false;
		}
		return Boolean.parseBoolean(value);
	}

	@Deprecated
	private void response302Header(DataOutputStream dos, String url) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	@Deprecated
	private int getContentLength(String line) {
    	String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}

}
