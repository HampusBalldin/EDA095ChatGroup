package org.violin.dynamic;

import java.io.IOException;
import java.io.OutputStream;

import org.violin.Cookies;
import org.violin.HTTPUtilities;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class Login implements Action {
	private Database db;
	private AsyncHandlerManager manager;
	private Cookies cookies = new Cookies();

	public Login(Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;

	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		DBUsers dbUsers = new DBUsers(db);
		if (dbUsers.authenticate(user)) {
			System.out.println("AUTHENTICATED");
			dbLogin(user); // loggar in i databasen
			createContext(user); // skapar context
			cookies.setCookie(user, exchange.getResponseHeaders()); // sätter
																	// cookie
			String response = "";
			try {
				System.out.println("SENDING RESPONSE HEADERS AND BODY: ");
				HTTPUtilities.printHeaders(exchange.getResponseHeaders());
				exchange.sendResponseHeaders(200, response.length());
				OutputStream os = exchange.getResponseBody();
				os.write(response.getBytes());
				os.close();

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

	private void dbLogin(User user) {
		DBUsers dbUsers = new DBUsers(db);
		user.setStatus(Status.ONLINE);
		dbUsers.update(user);
	}

	private void createContext(User user) {
		manager.createContext(user);
	}
}
