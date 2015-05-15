package org.violin;

import java.io.IOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {
	public void handle(HttpExchange exchange) throws IOException {
		Headers response = exchange.getResponseHeaders();
		response.set("Refresh", "0; url=127.0.0.1:8080/t/main/index.html");
		response.set("Host", "127.0.0.1:8080");
		response.set("Location", "127.0.0.1:8080/r/main/index.html");
		exchange.sendResponseHeaders(301, response.size());
		exchange.getResponseBody();
	}
}
