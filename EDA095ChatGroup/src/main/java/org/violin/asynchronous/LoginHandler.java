package org.violin.asynchronous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.XML;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class LoginHandler extends AsyncHandler {

	public LoginHandler(HttpServer server) {
		super(server);
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("LoginHandler: " + exchange.getRequestURI());
		switch (exchange.getRequestMethod().toUpperCase()) {
		case "GET":
			handleGet(exchange);
			break;
		case "POST":
			handlePost(exchange);
			break;
		}
	}

	public void handleGet(HttpExchange exchange) {

	}

	public void handlePost(HttpExchange exchange) {
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
		User user = getUser(sb.toString());
		System.out.println(user.getUid() + user.getPwd() + user.getStatus());
	}

	public User getUser(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		String xml = XML.toString(jsonObject);
		User user = null;
		try {
			Users users = XMLUtilities.unmarshal(XMLUtilities.documentify(xml),
					Users.class, ObjectFactory.class);
			if (users.getUser().size() >= 1) {
				user = users.getUser().get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user;
	}

}
