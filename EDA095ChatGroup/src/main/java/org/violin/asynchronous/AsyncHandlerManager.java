package org.violin.asynchronous;

import java.util.ArrayList;
import java.util.HashMap;

import org.violin.database.generated.Message;
import org.violin.database.generated.User;

public class AsyncHandlerManager {

	private HashMap<String, AsyncHandler> contexts = new HashMap<String, AsyncHandler>();

	public AsyncHandlerManager() {
	}

	public void createContext(String path, AsyncHandler handler) {
		contexts.put(path, handler);
	}

	public void removeContext(String path) { // synchronized (contexts)?
		contexts.remove(path);
	}

	public void distributeMessage(Message msg) {
		ArrayList<User> users = (ArrayList<User>) msg.getDestinations().getUser();
		for (User user : users) {
			AsyncHandler handler = contexts.get(user.getUid());
			handler.receiveMessage(msg);
		}
	}

	// @Override
	// public void handle(HttpExchange exchange) throws IOException {
	// String path = exchange.getRequestURI().getPath();
	// AsyncHandler handler = contexts.get(path);
	// if (handler != null) {
	// handler.handle(exchange);
	// } else {
	// System.out.println("No handler found at: " + path);
	// }
	// }

}
