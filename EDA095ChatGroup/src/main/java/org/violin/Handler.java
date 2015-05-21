package org.violin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.User;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler {
	protected Database db = new Database();
	protected DBUsers dbUsers = new DBUsers(db);

	protected boolean authenticate(User user) {
		if (dbUsers.authenticate(user)) {
			return true;
		} else {
			return false;
		}
	}

	protected User createUser(HttpExchange exchange)
			throws NullPointerException {
		User user = new User();
		Headers headers = exchange.getResponseHeaders();
		ArrayList<String> cookies = new ArrayList<String>();
		cookies = (ArrayList<String>) headers.get("Cookie");
		String[] cookie = null;
		cookie = cookies.get(0).split(";");
		String uid = cookie[0];
		String pwd = cookie[1];
		user.setUid(uid);
		user.setPwd(pwd);
		return user;
	}

	protected String getExchangeContent(HttpExchange exchange) {
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
		return sb.toString();
	}

	protected Message createMessage(String jsonString) {
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
