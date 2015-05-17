package org.violin.asynchronous;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

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

		System.out.println("Headers: ");
		HTTPUtilities.printHeaders(exchange.getRequestHeaders());
		List<String> cookie = exchange.getRequestHeaders().get("Cookie");
		System.out.println();
		List<String> setCookie = exchange.getRequestHeaders().get("Set-Cookie");

		if (cookie != null) {
			Iterator<String> itr1 = cookie.iterator();
			System.out.println("Cookie");
			while (itr1.hasNext()) {
				System.out.println(itr1.next());
			}
		} else {
			System.out.println("Cookie Null!");
		}

		if (setCookie != null) {
			Iterator<String> itr2 = setCookie.iterator();
			System.out.println("Set-Cookie");
			while (itr2.hasNext()) {
				System.out.println(itr2.next());
			}
			try {
				super.handle(exchange);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Set-Cookie Null!!");
		}
	}
}
