package org.violin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
	private Cookies cookieHandler = new Cookies();

	public Handler(Database db) {
		this.db = db;
	}

	/**
	 * Determines whether an incomming request is allowed or not.
	 * 
	 * @param exchange
	 * @return
	 */
	protected boolean authenticate(HttpExchange exchange) {
		System.out.println("BEGIN AUTHENTICATE");
		Headers reqHeaders = exchange.getRequestHeaders();
		String msg = getExchangeContent(exchange);
		DBUsers dbUsers = new DBUsers(db);
		User user = msg.equals("") ? extractUserFromCookies(reqHeaders, dbUsers)
				: extractUserFromMessage(msg, dbUsers, exchange);
		return dbUsers.authenticate(user);
	}

	private User extractUserFromCookies(Headers reqHeaders, DBUsers dbUsers) {
		System.out.println("EMPTY MESSAGE");
		User user = null;
		user = cookieHandler.extractUserFromCookies(reqHeaders, dbUsers);
		return user;
	}

	private User extractUserFromMessage(String msg, DBUsers dbUsers,
			HttpExchange exchange) {
		System.out.println("RECEIVE MESSAGE: [" + msg + "]");
		User user = null;
		try {
			user = getUserData(msg, dbUsers);
			cookieHandler.setCookie(user, exchange.getResponseHeaders());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return user;
	}

	/**
	 * Retrives user data from login form.
	 * 
	 * @param msg
	 * @return
	 * @throws IOException
	 */
	private User getUserData(String msg, DBUsers dbUsers) throws IOException {
		String[] tmp1 = msg.split("&");
		String uid = tmp1[0].split("=")[1];
		String pwd = tmp1[1].split("=")[1];
		return dbUsers.createUser(uid, pwd, Status.ONLINE);
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
}
