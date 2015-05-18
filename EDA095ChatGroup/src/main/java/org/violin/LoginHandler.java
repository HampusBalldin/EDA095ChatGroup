package org.violin;

import java.util.Iterator;

import org.violin.asynchronous.AsynchContexts;
import org.violin.asynchronous.AsynchHandler;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.HttpExchange;

public class LoginHandler extends StaticHandler {
	private Database db;
	private AsynchContexts contexts;

	public LoginHandler(Database db, AsynchContexts contexts) {
		super();
		this.db = db;
		this.contexts = contexts;
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("LoginHandler: " + exchange.getRequestURI());
		Message msg = getMessage(exchange);
		User user = msg.getOrigin();
		Users destinations = msg.getDestinations();
		Iterator<User> itr = destinations.getUser().iterator();
		while(itr.hasNext()){
			
			User u = itr.next();
			System.out.println(u.getPwd() + u.getUid() + u.getStatus().value());
		}
		
		createContext(user);
		notifyOnlineFriends(user);
		setCookie(user, exchange);
	}

	private void setCookie(User user, HttpExchange exchange) {
		exchange.getResponseHeaders().set("Cookie",
				"uid=" + user.getUid() + ";");
	}

	/**
	 * Creates an asynchronous context with
	 * org.violin.asynchronous.AsynchContexts
	 * 
	 * @param user
	 */
	private void createContext(User user) {

		contexts.createContext("/asynch/" + user.getUid(), new AsynchHandler(
				contexts));
	}

	public void notifyOnlineFriends(User user) {
		DBUsers dbUsers = new DBUsers(db);

		Users users = dbUsers.query("SELECT * " + " FROM Users"
				+ " WHERE (status = ? OR status = ?) AND (uid IN"
				+ " (SELECT uid_1 FROM friends"
				+ " WHERE ? = uid_2) OR uid IN " + " (SELECT uid_2"
				+ " FROM friends " + " WHERE ? = uid_1))",
				Status.ONLINE.value(), Status.AWAY.value(), "" + user.getUid(),
				"" + user.getUid());
		
		for (User friend : users.getUser()) {
			System.out.println(friend.getUid() + " - " + friend.getStatus());
		}
	}
}
