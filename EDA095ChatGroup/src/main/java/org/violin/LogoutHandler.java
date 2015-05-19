package org.violin;

import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.Database;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpExchange;

public class LogoutHandler extends StaticHandler { 
	Database db;
	AsyncHandlerManager manager;

	public LogoutHandler(Database db, AsyncHandlerManager manager) {
		super();
		this.db = db;
		this.manager = manager;
	}

	@Override
	public void handle(HttpExchange exchange) {  //System.out.println("LoginHandler: " + exchange.getRequestURI());
		String query = exchange.getRequestURI().getQuery();
		String uid = "";	//utvinn uid ur query
		String pwd = "";	//utvinn pwd ur query
		Status status = Status.OFFLINE;
		User user = new User();
		user.setUid(uid);
		user.setPwd(pwd);
		user.setStatus(status);
		
		logout(db);
		removeContext(user);

	}

	private void logout(Database db) {	//fix
		
	}
	
	private void removeContext(User user) {
		manager.removeContext("/asynch/" + user.getUid());
	}
	
}