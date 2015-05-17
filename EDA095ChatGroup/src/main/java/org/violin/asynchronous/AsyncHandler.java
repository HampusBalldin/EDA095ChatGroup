package org.violin.asynchronous;

import java.io.IOException;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AsyncHandler implements HttpHandler {
	/**
	 * The principal which this handler is responsible for...
	 */
	private String principal;
	protected HttpServer server;

	public AsyncHandler(HttpServer server) {
		this.server = server;
	}

	public void handle(HttpExchange exchange) throws IOException {
		Headers requestHeaders = exchange.getRequestHeaders();
	}

	public String getPrincipal() {
		return principal;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AsyncHandler))
			return false;
		return principal.equals(((AsyncHandler) obj).principal);
	}
	

}
