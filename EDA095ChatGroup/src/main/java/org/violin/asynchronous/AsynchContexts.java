package org.violin.asynchronous;

import java.io.IOException;
import java.util.HashMap;

import org.violin.database.Database;
import org.violin.database.generated.Message;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class AsynchContexts implements HttpHandler {

	private HashMap<String, AsynchHandler> contexts = new HashMap<String, AsynchHandler>();
	private Database db;

	public AsynchContexts(Database db) {
		this.db = db;
	}

	public void createContext(String binding, AsynchHandler handler) {
		synchronized (contexts) {
			contexts.put(binding, handler);
		}
	}

	public void removeContext(String binding) {
		synchronized (contexts) {
			contexts.remove(binding);
		}
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String path = exchange.getRequestURI().getPath();
		AsynchHandler handler = contexts.get(path);
		if (handler != null) {
			handler.handle(exchange);
		} else {
			System.out.println("No handler found at: " + path);
		}
	}

	public void addMessage(String binding, Message msg) {

	}
}
