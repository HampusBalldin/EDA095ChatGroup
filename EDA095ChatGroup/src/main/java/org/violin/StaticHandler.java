package org.violin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.violin.HTTPUtilities.MimeResolver;
import org.violin.database.Database;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class StaticHandler extends Handler {
	public StaticHandler(Database db) {
		super(db);
	}

	private static final MimeResolver resolver = new MimeResolver();

	private void handle(HttpExchange exchange, String path) throws IOException {
		System.out.println("StaticHandler" + path);
		boolean isAuthenticated = authenticate(exchange);
		if (isAuthenticated) {
			System.out.println("OFFICIALLY AUTHENTICATED");
		} else {
			System.out.println("OFFICIALLY NOT AUTHENTICATED");
		}
		if (isAuthenticated) {
			System.out.println("Authenticated!");
			System.out.println("Getting: " + path);
			System.out.println(exchange.getRequestURI());
			exchange.getResponseHeaders().set("Content-Type",
					resolver.resolveHttpContent(path));
			FileInputStream in = new FileInputStream(new File(path));
			HTTPUtilities.printHeaders(exchange.getResponseHeaders());
			exchange.sendResponseHeaders(200, 0);
			BufferedOutputStream os = new BufferedOutputStream(
					exchange.getResponseBody());
			int b = 0;
			while ((b = in.read()) != -1) {
				os.write(b);
			}
			os.flush();
			in.close();
		} else {
			System.out.println("Not Authenticated!");
			// Headers response = exchange.getResponseHeaders();
			// response.set("Location", "127.0.0.1:8080/chat/index.html");
			// exchange.sendResponseHeaders(301, response.size());
		}
		exchange.close();
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("STATIC HANDLER" + exchange.getRequestURI());
		String path = System.getProperty("user.dir") + "/src/main/resources"
				+ exchange.getRequestURI();
		handle(exchange, path);
	}
}
