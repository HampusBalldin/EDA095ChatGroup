package org.violin.asynchronous;

import java.util.ArrayList;
import java.util.HashMap;

import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.User;

import com.sun.net.httpserver.HttpServer;

public class AsyncHandlerManager {

	private Database db;
	private HashMap<String, AsyncHandler> contexts;
	private HttpServer httpServer;

	public AsyncHandlerManager(Database db, HttpServer httpServer) {
		this.db = db;
		contexts = new HashMap<String, AsyncHandler>();
		this.httpServer = httpServer;
	}

	public void createContext(User user) {
		String context = "/asynch/" + user.getUid();
		System.out.println("CREATING CONTEXT: " + context);
		if (!contexts.containsKey(context)) {
			AsyncHandler asynchHandler = new AsyncHandler(db, this);
			httpServer.createContext(context, asynchHandler);
			contexts.put(context, asynchHandler);
			asynchHandler.start();
		}
	}

	public void removeContext(User user) {
		String context = "/asynch/" + user.getUid();
		synchronized (contexts) {
			AsyncHandler handler = contexts.get(context);
			handler.terminate();
			contexts.remove(context);
			httpServer.removeContext(context);
		}
	}

	public boolean hasBinding(User user) {
		synchronized (contexts) {
			String context = "/asynch/" + user.getUid();
			return contexts.containsKey(context);
		}
	}

	public void distributeMessage(Message msg) {
		ArrayList<User> users = (ArrayList<User>) msg.getDestinations()
				.getUser();
		for (User user : users) {
			String context = "/asynch/" + user.getUid();
			System.out.println("AsynchHandler Distribute Message for context "
					+ context);
			AsyncHandler handler = contexts.get(context);
			handler.receiveMessage(msg);
		}
	}
}
