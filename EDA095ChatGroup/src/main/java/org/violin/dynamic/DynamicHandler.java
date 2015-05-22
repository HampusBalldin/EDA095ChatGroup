package org.violin.dynamic;

import java.io.IOException;
import java.io.OutputStream;

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

import com.sun.net.httpserver.HttpExchange;

public class DynamicHandler extends Handler {
	private Database db;
	private AsyncHandlerManager manager;
	private DBUsers dbUsers;

	public DynamicHandler(Database db, AsyncHandlerManager manager) {
		super(db);
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
		if (msg != null) {
			switch (msg.getType()) {
			case LOGIN:
				System.out.println("Dynamic Handler LOGIN");
				handleLogin(msg, exchange);
				break;
			case LOGOUT:
				System.out.println("Dynamic Handler LOGOUT");
				handleLogout(msg, exchange);
				break;
			case GET_FRIENDS: // skicka users
				System.out.println("Dynamic Handler GET FRIENDS");
				handleGetFriends(msg, exchange);
				break;
			case CHECK_CONNECTION_STATUS:
				handleCheckConnectionStatus(msg, exchange);
				break;
			}
		} else {
			System.out
					.println("DynamicHandler: Message is null,  Cannot Handle!");
		}
		exchange.close();
	}

	private void handleCheckConnectionStatus(Message msg, HttpExchange exchange) {
		try {
			boolean connected = manager.hasBinding(msg.getOrigin());
			exchange.sendResponseHeaders(200, 0);
			String reply = connected ? "TRUE" : "FALSE";
			exchange.getResponseBody().write(reply.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void handleLogin(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		if (dbUsers.authenticate(user)) {
			System.out.println("AUTHENTICATED");
			dbLogin(user); // loggar in i databasen
			createContext(user); // skapar context
			setCookie(user, exchange); // sätter cookie
			try {
				System.out.println("SENDING RESPONSE HEADERS: ");
				HTTPUtilities.printHeaders(exchange.getResponseHeaders());
				exchange.sendResponseHeaders(200, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("NOT AUTHENTICATED");
			try {
				exchange.sendResponseHeaders(401, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void handleLogout(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		dbLogout(user); // loggar ut ur databaen
		removeContext(user); // tar bort context

		try {
			
				String response = "<html lang=\"en\"><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>Logout</title><meta name=\"author\" content=\"David\" /><!-- Date: 2015-05-22 --></head><body><h2>You have been logged out</h2><form method=\"post\" action=\"/login/index.html\"><p class=\"submit\"><input type=\"submit\" value=\"Return to login\">	</p></form></body></html>"; 
				System.out.println("SENDING RESPONSE HEADERS AND BODY: ");
				HTTPUtilities.printHeaders(exchange.getResponseHeaders());
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();		
				
				} catch (IOException e) {
					
			e.printStackTrace();
		}
	}

	private void handleGetFriends(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		if (dbUsers.authenticate(user)) {
			Users users = getFriends(user);
			System.out.println("GOT THEM FRIENDS!");
			try {
				System.out.println("SENDING RESPONSE HEADERS!");
				HTTPUtilities.printHeaders(exchange.getResponseHeaders());
				exchange.sendResponseHeaders(200, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			OutputStream os = exchange.getResponseBody();
			sendFriends(os, users); // gör till xml-sträng, skickar
									// strängen
		} else {
			System.out.println("NOT AUTHENTICATED");
			try {
				exchange.sendResponseHeaders(401, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
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

	private Users getFriends(User user) {
		return dbUsers.getFriends(user);
	}

	private Users getOnlineFriends(User user) {
		return dbUsers.getOnlineFriends(user);
	}

	private void sendFriends(OutputStream os, Users users) {
		System.out.println("----------SENDING FRIENDS --------");
		DBUsers dbUsers = new DBUsers(db);
		System.out.println(dbUsers.stringify(users));

		QName qName = new QName("Users");
		JAXBElement<Users> jaxbElement = new JAXBElement<Users>(qName,
				Users.class, users);
		try {
			XMLUtilities.marshal(jaxbElement, ObjectFactory.class, os);
			os.flush();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
