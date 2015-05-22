package org.violin.dynamic;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.HTTPUtilities;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.HttpExchange;

public class GetFriends implements Action {
	private Database db;

	public GetFriends(Database db) {
		this.db = db;
	}

	private Users getFriends(User user) {
		DBUsers dbUsers = new DBUsers(db);
		return dbUsers.getFriends(user);
	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		DBUsers dbUsers = new DBUsers(db);
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
			sendFriends(os, users);
		} else {
			System.out.println("NOT AUTHENTICATED");
			try {
				exchange.sendResponseHeaders(401, -1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
