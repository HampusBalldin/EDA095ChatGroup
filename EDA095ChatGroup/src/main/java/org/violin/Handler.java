package org.violin;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public abstract class Handler implements HttpHandler {
	public boolean authenticate(HttpExchange exchange) {
		return true;
	}
}
