package org.violin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
		System.out.println("BEGIN AUTHENTICATE");
		Headers reqHeaders = exchange.getRequestHeaders();
		String msg = getExchangeContent(exchange);
		User user = null;
		if (msg.equals("")) {
			System.out.println("EMPTY MESSAGE_1");
			user = createUser(reqHeaders);
		} else {
			System.out.println("RECEIVE MESSAGE: [" + msg + "]");
			try {
				user = getUserData(msg);
				setCookie(user, exchange.getResponseHeaders());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		boolean authenticated = false;
		try {
			DBUsers dbUsers = new DBUsers(db);
			authenticated = dbUsers.authenticate(user);
			System.out.println("AUTHENTICATED: " + authenticated);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return authenticated;
	}

	private User getUserData(String msg) throws IOException {
		String[] tmp1 = msg.split("&");
		String uid = tmp1[0].split("=")[1];
		String pwd = tmp1[1].split("=")[1];
		DBUsers dbUsers = new DBUsers(db);
		return dbUsers.createUser(uid, pwd, Status.ONLINE);
	}

	private User createUser(Headers reqHeaders) throws NullPointerException {
		System.out.println("CREATE USER");
		User user = new User();
		if (reqHeaders != null) {

		} else {
			System.out.println("REQHEADERS ARE NULL!");
		}
		HTTPUtilities.printHeaders(reqHeaders);
		System.out.println("GETTING Cookies");
		List<String> cookies = reqHeaders.get("Cookie");
		if (cookies != null) {
			System.out.println("ArrayList<String> size = " + cookies.size());
			String[] cookie = cookies.get(0).split(";");
			System.out.println("RECEIVED " + cookie.length + " COOKIES");
			if (cookie.length >= 2) {
				String uid = cookie[0].split("=")[1];
				String pwd = cookie[1].split("=")[1];
				user.setUid(uid);
				user.setPwd(pwd);
				user.setStatus(Status.ONLINE);
				System.out.println("Created User " + user.getUid()
						+ user.getPwd());
			} else {
			}
		} else {
			System.out.println("COOKIES ARE NULL");
		}
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

	public void setCookie(User user, Headers headers) {
		List<String> values = new ArrayList<String>();
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 5);
		dt = c.getTime();
		System.out.println("; expires 	= " + dt.toString());
		values.add("uid=" + user.getUid() + "; expires=" + dt.toString()
				+ ";path=/;");
		values.add("pwd=" + user.getPwd() + "; expires=" + dt.toString()
				+ ";path=/;");
		headers.put("Set-Cookie", values);
		headers.set("Access-Control-Max-Age", "360");
		headers.set("Access-Control-Allow-Credentials", "true");
		headers.set("Access-Control-Allow-Headers", "Authorization");
	}
}
