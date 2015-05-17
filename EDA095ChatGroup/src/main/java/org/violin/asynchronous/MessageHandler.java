package org.violin.asynchronous;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class MessageHandler extends AsyncHandler {

	public MessageHandler(HttpServer server) {
		super(server);
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("MessageHandler: " + exchange.getRequestURI());
		super.handle(exchange);
	}
}
