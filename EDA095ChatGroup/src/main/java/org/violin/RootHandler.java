package org.violin;

import java.io.IOException;
import java.net.Inet4Address;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RootHandler implements HttpHandler {

	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("ROOT: " + exchange.getRequestURI());
		System.out.println("WE HAVE " + Inet4Address.getLocalHost());
		Headers response = exchange.getResponseHeaders();
		response.set("Host", Inet4Address.getLocalHost().getHostAddress()
				+ ":8080");
		response.set("Location", Inet4Address.getLocalHost().getHostAddress()
				+ ":8080/login/index.html");
		exchange.sendResponseHeaders(301, response.size());
	}
}
