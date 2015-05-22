package org.violin.dynamic;

import java.io.IOException;

import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.generated.Message;

import com.sun.net.httpserver.HttpExchange;

public class CheckConnection implements Action {
	private AsyncHandlerManager manager;

	public CheckConnection(AsyncHandlerManager manager) {
		this.manager = manager;
	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		try {
			boolean connected = manager.hasBinding(msg.getOrigin());
			exchange.sendResponseHeaders(200, 0);
			String reply = connected ? "TRUE" : "FALSE";
			exchange.getResponseBody().write(reply.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
