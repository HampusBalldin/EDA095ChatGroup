package org.violin;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;

public class LoginpageStaticHandler extends StaticHandler {
	private RootHandler rootHandler;

	public LoginpageStaticHandler(RootHandler rootHandler) {
		this.rootHandler = rootHandler;
	}

	public void handle(HttpExchange exchange) throws IOException {
		if (isAuhtenticated()) {
			super.handle(exchange);
		} else {
			rootHandler.handle(exchange);
		}
	}

	public boolean isAuhtenticated() {
		return true;
	}
}
