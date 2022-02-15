package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import util.IOUtils;


public class HttpRequest {
	
	private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);
	
	private String method;
	private String path;
	private Map<String, String> headers = new HashMap<String, String>();
	private Map<String, String> params = new HashMap<String, String>();	
	private RequestLine requestLine;
	
	public HttpRequest() {
		
	}
	
	/**
	 * @param InputStream in
	 * @author szw
	 */
	public HttpRequest(InputStream in) {
		try {
			BufferedReader br = 
					new BufferedReader(new InputStreamReader(in, "UTF-8"));
			String line = br.readLine();
			if (line == null) {
				return;
			}
			
			processRequestLine(line);
			
			line = br.readLine();
			while (!line.equals("")) {
				log.debug("header : {}", line);		
				String[] tokens =  line.split(":");
				headers.put(tokens[0].trim(), tokens[1].trim());
				line = br.readLine();
			}				
			
			requestLine = new RequestLine(line); 
			
			if ("POST".equals(method)) {
				String body = 
						IOUtils.readData(br, 
								Integer.parseInt(headers.get("Content-Length")));
				params = HttpRequestUtils.parseQueryString(body);
			} else {
				params = requestLine.getParams();
			}
		} catch (IOException io) {
			log.error(io.getMessage());
		}
	}

	
	/**
	 * @param String requestLine 
	 * @author szw
	 */
	private void processRequestLine(String requestLine) {
		log.debug("request line : {}", requestLine);
		String[] tokens = requestLine.split(" ");
		method = tokens[0];
		
		if ("POST".equals(method)) {
			path = tokens[1];
			return;
		}
		
		int index = tokens[1].indexOf("?");
		if (index == -1) {
			path = tokens[1];
		} else {
			path = tokens[1].substring(0, index);
			params = HttpRequestUtils.parseQueryString(
					tokens[1].substring(index + 1));
		}
	}

	
	/**
	 * @return String method
	 */
	public String getMethod() {
		return requestLine.getMethod();
	}

	/**
	 * @return String path
	 */
	public String getPath() {
		return requestLine.getPath();
	}

	/**
	 * @return String headerName
	 */
	public String getHeader(String name) {
		log.debug("headers : {}", headers.get(name));
		return headers.get(name);
	}

	/**
	 * @return String parameterName
	 */
	public String getParameter(String name) {
		return params.get(name);
	}

}
