package org.violin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler {
	private Database db;

	public Handler(Database db) {
		this.db = db;
	}

	protected boolean authenticate(HttpExchange exchange) {
		User user = getUser(exchange);
		DBUsers dbUsers = new DBUsers(db);
		if (user != null) {
			if (dbUsers.authenticate(user)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private User getUser(HttpExchange exchange) {
		User user = null;
		try {
			user = createUser(exchange);
		} catch (NullPointerException e) {
			try {
				user = getUserData(exchange);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			setCookie(user, exchange);
		}
		return user;
	}

	private User getUserData(HttpExchange exchange) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				exchange.getRequestBody()));
		while (br.ready()) {
			sb.append(br.readLine());
		}
		String msg = sb.toString();
		String[] tmp1 = msg.split("&");
		String uid = tmp1[0].split("=")[1];
		String pwd = tmp1[1].split("=")[1];
		DBUsers dbUsers = new DBUsers(db);
		return dbUsers.createUser(uid, pwd, Status.ONLINE);
	}

	private User createUser(HttpExchange exchange) throws NullPointerException {
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
		user.setStatus(Status.ONLINE);
		return user;
	}

	protected String getExchangeContent(HttpExchange exchange) {
		System.out.println("GET EXCHANGE CONTENT");
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

	protected void setCookie(User user, HttpExchange exchange) {
		Headers headers = exchange.getResponseHeaders();
		List<String> values = new ArrayList<String>();
		values.add("uid=" + user.getUid());
		values.add("pwd=" + user.getPwd());
		headers.put("Set-Cookie", values);
	}
}
