package http;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import http.HttpRequest;
import http.RequestLine;

public class HttpRequestTest {

	private String testDirectory = "./src/test/resources/";
	
	@Before
	public void setUp() throws Exception {
		
	}

	@Test
	public void request_GET() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "Http_GET.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("GET", request.getMethod());		
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
	}
	
	@Test
	public void request_POST() throws Exception {
		InputStream in = new FileInputStream(new File(testDirectory + "Http_POST.txt"));
		HttpRequest request = new HttpRequest(in);
		
		assertEquals("POST", request.getMethod());		
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("javajigi", request.getParameter("userId"));
		
	}
	
	/*
	@Test
	public void responseForward() throws Exception {
		// Http_Forward.txt 결과는 응답 body에 index.html이 포함되어 있어야 한다
		HttpResponse response = new HttpResponse(createOutputStream("Http_Forward.txt"));
		response.forward("/index.html");
	}
	

	@Test
	public void responseRedirect() throws Exception {
		// Http_Redirect.txt 결과는 응답 header에
		// Location 정보가 /index.html로 포함되어 있어야 한다.
		
		HttpResponse response = 
				new HttpResponse(createOutputStream("Http_Redirect.txt"));
		response.sendRedirect("/index.html");
	}
	
	@Test
	public void responseCookies() throws Exception {
		// Http_Cookie.txt 결과는 응답 header에 Set-Cookie 값으로
		// logined=true 값이 포함되어 있어야 한다.
		HttpResponse response = 
				new HttpResponse(createOutputStream("Http_Cookie.txt"));
		response.addHeader("Set-Cookie", "logined=true");
		response.sendRedirect("/index.html");
	}
	
	private OutputStream createOutputStream(String filename) 
			throws FileNotFoundException {
		return new FileOutputStream(new File(testDirectory + filename));
	}
	 * 
	 * */
	
	
	@Test
	public void create_method() {
		RequestLine line = new RequestLine("GET /index.html HTTP/1.1");
		assertEquals("GET", line.getMethod());
		assertEquals("/index.html", line.getPath());
		
		line = new RequestLine("POST /index.html HTTP/1.1");
		assertEquals("/index.html", line.getPath());
	}
	
	@Test
	public void create_path_and_params() {
		RequestLine line = new RequestLine(
				"GET /user/create?userId=javajigi&password=pass HTTP/1.1");
		assertEquals("GET", line.getMethod());
		assertEquals("/user/create", line.getPath());
		Map<String, String> params = line.getParams();
		assertEquals(2, params.size());
	}
}

