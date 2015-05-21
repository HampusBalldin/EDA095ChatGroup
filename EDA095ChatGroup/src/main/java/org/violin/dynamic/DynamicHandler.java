package org.violin.dynamic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.HTTPUtilities;
import org.violin.Handler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class DynamicHandler extends Handler {
	private Database db;
	private AsyncHandlerManager manager;
	private DBUsers dbUsers;

	public DynamicHandler(Database db, AsyncHandlerManager manager) {
		super();
		this.db = db;
		this.manager = manager;
		dbUsers = new DBUsers(db);
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("Dynamic Handler");

		HTTPUtilities.printHeaders(exchange.getRequestHeaders());
		String exchangeContent = getExchangeContent(exchange);
		Message msg = createMessage(exchangeContent);
		User user;
		switch (msg.getType()) {
		case LOGIN:
			System.out.println("Dynamic Handler LOGIN");
			user = msg.getOrigin();
			if (dbUsers.authenticate(user)) {
				System.out.println("AUTHENTICATED");
				dbLogin(user); // loggar in i databasen
				createContext(user); // skapar context
				setCookie(user, exchange); // sätter cookie
				break;
			} else {
				System.out.println("NOT AUTHENTICATED");
				break;
			}
		case LOGOUT:
			System.out.println("Dynamic Handler LOGOUT");
			user = createUser(exchange);
			dbLogout(user); // loggar ut ur databaen
			removeContext(user); // tar bort context
			break;
		case GET_FRIENDS: // skicka users
			System.out.println("Dynamic Handler GET FRIENDS");
			user = createUser(exchange);
			Users users = getFriends(user);
			OutputStream os = exchange.getResponseBody();
			sendFriends(os, users); // gör till xml-sträng, skickar strängen
			break;
		}
		try {
			System.out.println("SENDING RESPONSE HEADERS!");
			HTTPUtilities.printHeaders(exchange.getResponseHeaders());
			exchange.sendResponseHeaders(200, -1);
			exchange.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dbLogin(User user) {
		user.setStatus(Status.ONLINE);
		dbUsers.update(user);
	}

	private void dbLogout(User user) {
		user.setStatus(Status.OFFLINE);
		dbUsers.update(user);
	}

	private void createContext(User user) {
		manager.createContext(user);
	}

	private void removeContext(User user) {
		manager.removeContext(user);
	}

	private void setCookie(User user, HttpExchange exchange) {
		Headers headers = exchange.getResponseHeaders();
		List<String> values = new ArrayList<String>();
		values.add("uid=" + user.getUid() + ";");
		values.add("pwd=" + user.getPwd() + ";");
		headers.put("Set-Cookie", values);
	}

	private Users getFriends(User user) {
		return dbUsers.getFriends(user);
	}

	private Users getOnlineFriends(User user) {
		return dbUsers.getOnlineFriends(user);
	}

	private void sendFriends(OutputStream os, Users users) {
		QName qName = new QName("Users");
		JAXBElement<Users> jaxbElement = new JAXBElement<Users>(qName,
				Users.class, users);
		try {
			XMLUtilities.marshal(jaxbElement, ObjectFactory.class, os);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

}
