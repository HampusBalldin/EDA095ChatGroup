package org.violin.asynchronous;

import java.util.ArrayList;
import java.util.HashMap;

import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.User;

public class AsyncHandlerManager {
	
	private Database db;
	private HashMap<String, AsyncHandler> contexts;

	public AsyncHandlerManager(Database db) {
		this.db = db;
		contexts = new HashMap<String, AsyncHandler>();
	}

	public void createContext(User user) {
		String context = "/asynch/" + user.getUid();
		if (!contexts.containsKey(context)) { 
			contexts.put(context, new AsyncHandler(db, this));
		}
	}

	public void removeContext(User user) { 						// synchronized (contexts)?
		String context = "/asynch/" + user.getUid();
		contexts.remove(context);
	}

	public void distributeMessage(Message msg) {
		ArrayList<User> users = (ArrayList<User>) msg.getDestinations().getUser();
		for (User user : users) {
			AsyncHandler handler = contexts.get(user.getUid());
			handler.receiveMessage(msg);
		}
	}


}
