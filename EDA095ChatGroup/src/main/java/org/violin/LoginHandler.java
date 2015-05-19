package org.violin;

import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class LoginHandler extends StaticHandler { 
	Database db;
	AsyncHandlerManager manager;
	DBUsers users;

	public LoginHandler(Database db, AsyncHandlerManager manager) {
		super();
		this.db = db;
		this.manager = manager;
		users = new DBUsers(db);
	}

	@Override
	public void handle(HttpExchange exchange) {  				//System.out.println("LoginHandler: " + exchange.getRequestURI());
		String query = exchange.getRequestURI().getQuery();
		String uid = "";	//utvinn uid ur query
		String pwd = "";	//utvinn pwd ur query
		Status status = Status.ONLINE;
		User user = new User();
		user.setUid(uid);
		user.setPwd(pwd);
		user.setStatus(status);
		
		dbLogin(user);
		createContext(user);
		setCookie(user, exchange);
	}

	private void dbLogin(User user) {
		users.update(user);
	}
	
	private void createContext(User user) {
		manager.createContext(user);
	}
	
	private void setCookie(User user, HttpExchange exchange) {
		exchange.getResponseHeaders().set("Cookie", "uid=" + user.getUid() + ";");
	}

}
