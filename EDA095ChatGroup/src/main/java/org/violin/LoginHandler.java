package org.violin;

import org.violin.asynchronous.AsyncHandler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.Database;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class LoginHandler extends StaticHandler { 
	Database db;
	AsyncHandlerManager manager;

	public LoginHandler(Database db, AsyncHandlerManager manager) {
		super();
		this.db = db;
		this.manager = manager;
	}

	@Override
	public void handle(HttpExchange exchange) {  //System.out.println("LoginHandler: " + exchange.getRequestURI());
		String query = exchange.getRequestURI().getQuery();
		String uid = "";	//utvinn uid ur query
		String pwd = "";	//utvinn pwd ur query
		Status status = Status.ONLINE;
		User user = new User();
		user.setUid(uid);
		user.setPwd(pwd);
		user.setStatus(status);
		
		login(db);
		createContext(user);
		notifyOnlineFriends(user);		//?
		setCookie(user, exchange);		//?
	}

	private void login(Database db) {	//fix
		db.
	}
	
	private void notifyOnlineFriends(User user) {	//vad ska ske här?
	
	}
	
	private void setCookie(User user, HttpExchange exchange) {	//?
		exchange.getResponseHeaders().set("Cookie",
				"uid=" + user.getUid() + ";");
	}

	private void createContext(User user) {
		manager.createContext("/asynch/" + user.getUid(), new AsyncHandler(db));
	}

}
