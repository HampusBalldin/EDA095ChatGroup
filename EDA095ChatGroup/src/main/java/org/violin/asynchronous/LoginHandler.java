package org.violin.asynchronous;

import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class LoginHandler extends AsyncHandler {
	private Database db;

	public LoginHandler(Database db, HttpServer server) {
		super(server);
		this.db = db;
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("LoginHandler: " + exchange.getRequestURI());
		Message msg = getMessage(exchange);
		createContext(msg.getOrigin());
		notifyOnlineFriends(msg.getOrigin());
		setCookie(msg.getOrigin(), exchange);
	}

	private void setCookie(User user, HttpExchange exchange) {
		exchange.getResponseHeaders().set("Cookie",
				"uid=" + user.getUid() + ";");
	}

	private void createContext(User user) {
		server.createContext("/async/" + user.getUid() + "/messageHandler",
				new MessageHandler(server));
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
