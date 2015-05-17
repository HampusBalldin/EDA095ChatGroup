package org.violin.asynchronous;

import java.io.BufferedInputStream;
import java.io.IOException;

import org.violin.HTTPUtilities;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class LoginHandler extends AsyncHandler {

	public LoginHandler(HttpServer server) {
		super(server);
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("LoginHandler: " + exchange.getRequestURI());
		switch (exchange.getRequestMethod().toUpperCase()) {
		case "GET":
			handleGet(exchange);
			break;
		case "POST":
			handlePost(exchange);
			break;
		}
	}

	public void handleGet(HttpExchange exchange) {

	}

	public void handlePost(HttpExchange exchange) {
		BufferedInputStream in = new BufferedInputStream(
				exchange.getRequestBody());
		int r = 0;
		try {
			while ((r = in.read()) != -1) {
				System.out.println(r);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
