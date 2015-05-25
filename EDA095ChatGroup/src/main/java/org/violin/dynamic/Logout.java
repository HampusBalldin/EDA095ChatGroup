package org.violin.dynamic;

import java.io.BufferedReader;
import java.io.FileReader;
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

public class Logout implements Action {
	private Database db;
	private AsyncHandlerManager manager;
	private Cookies cookieHandler = new Cookies();

	public Logout(Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;
	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		User user = msg.getOrigin();
		dbLogout(user); // loggar ut ur databaen
		removeContext(user); // tar bort context
		cookieHandler.deleteCookies(user,
				exchange.getResponseHeaders());
		try {
			StringBuilder contentBuilder = new StringBuilder();
			try {
				String path = System.getProperty("user.dir")
						+ "/src/main/resources/logout/logout.html";
				BufferedReader in = new BufferedReader(new FileReader(path));
				String str;
				while ((str = in.readLine()) != null) {
					contentBuilder.append(str);
				}
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			String response = contentBuilder.toString();
			exchange.sendResponseHeaders(200, response.length());
			OutputStream os = exchange.getResponseBody();
			os.write(response.getBytes());
			os.close();
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
