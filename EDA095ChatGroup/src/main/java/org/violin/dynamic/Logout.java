package org.violin.dynamic;

import java.io.IOException;

import org.violin.Handler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class Logout implements Action {
	private Database db;
	private AsyncHandlerManager manager;

	public Logout(Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;
	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		dbLogout(user); // loggar ut ur databaen
		removeContext(user); // tar bort context
		try {
			exchange.sendResponseHeaders(200, -1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void dbLogout(User user) {
		DBUsers dbUsers = new DBUsers(db);
		user.setStatus(Status.OFFLINE);
		dbUsers.update(user);
	}

	private void removeContext(User user) {
		manager.removeContext(user);
	}
}
