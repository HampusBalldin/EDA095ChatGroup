package org.violin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.XML;
import org.violin.HTTPUtilities.MimeResolver;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class StaticHandler extends Handler {
	private static final MimeResolver resolver = new MimeResolver();
	private String loginPath = System.getProperty("user.dir")
			+ "/src/main/resources/login/index.html";
	private String loginCss = System.getProperty("user.dir")
			+ "/src/main/resources/login/PageStyle.css";
	private String loginHXML = System.getProperty("user.dir")
			+ "/src/main/resources/javascripts/HXML.js";
	private String loginjquery = System.getProperty("user.dir")
			+ "/src/main/resources/javascripts/jquery.js";

	public void handle(HttpExchange exchange, String path) throws IOException {
		User user = null;
		boolean existsCookie = true;
		try {
			user = createUser(exchange);
		} catch (NullPointerException e) {
			existsCookie = false;
		}
		if (existsCookie) {
			if (authenticate(user)) {
				System.out.println("Authenticated!");
				System.out.println(path);
				System.out.println(exchange.getRequestURI());
				exchange.getResponseHeaders().set("Content-Type",
						resolver.resolveHttpContent(path));
				FileInputStream in = new FileInputStream(new File(path));
				HTTPUtilities.printHeaders(exchange.getResponseHeaders());
				exchange.sendResponseHeaders(200, 0);
				BufferedOutputStream os = new BufferedOutputStream(
						exchange.getResponseBody());
				int b = 0;
				while ((b = in.read()) != -1) {
					os.write(b);
				}
				os.flush();
				os.close();
				in.close();
			}
		} else {
			System.out.println("Not Authenticated!");
			sendbackLoginPage(exchange, path);
		}
	}

	public void sendbackLoginPage(HttpExchange exchange, String path)
			throws IOException {
		if (path.equals(loginPath)) {
			path = loginPath;
		} else if (path.equals(loginCss)) {
			path = loginCss;
		} else if (path.equals(loginHXML)) {
			path = loginHXML;
		} else if (path.equals(loginjquery)) {
			path = loginjquery;
		} else {
			path = loginPath;
		}
		exchange.getResponseHeaders().set("Content-Type",
				resolver.resolveHttpContent(path));
		FileInputStream in = new FileInputStream(new File(path));
		HTTPUtilities.printHeaders(exchange.getResponseHeaders());
		exchange.sendResponseHeaders(200, 0);
		BufferedOutputStream os = new BufferedOutputStream(
				exchange.getResponseBody());
		int b = 0;
		while ((b = in.read()) != -1) {
			os.write(b);
		}
		os.flush();
		os.close();
		in.close();
	}

	public void handle(HttpExchange exchange) throws IOException {
		String path = System.getProperty("user.dir") + "/src/main/resources"
				+ exchange.getRequestURI();
		handle(exchange, path);
	}

	protected Message getMessage(HttpExchange exchange) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				exchange.getRequestBody()));
		StringBuilder sb = new StringBuilder();
		try {
			while (in.ready()) {
				sb.append(in.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(sb.toString());
		return getMessage(sb.toString());
	}

	private Message getMessage(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		String xml = XML.toString(jsonObject);
		System.out.println(jsonString);
		System.out.println(xml);
		Message msg = null;
		try {
			msg = XMLUtilities.unmarshal(XMLUtilities.documentify(xml),
					Message.class, ObjectFactory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
}
