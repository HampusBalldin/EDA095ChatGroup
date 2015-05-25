package org.violin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.violin.HTTPUtilities.MimeResolver;
import org.violin.database.Database;

import com.sun.net.httpserver.HttpExchange;

public class StaticHandler extends Handler {
	public StaticHandler(Database db) {
		super(db);
	}

	private static final MimeResolver resolver = new MimeResolver();
	private static final String notAuthenticatedURL = System
			.getProperty("user.dir")
			+ "/src/main/resources/login/notauthorized.html";

	private void handle(HttpExchange exchange, String path) throws IOException {
		System.out.println("StaticHandler" + path);
		if (authenticate(exchange)) {
			System.out.println("Authenticated!");
			System.out.println("Getting: " + path);
			System.out.println(exchange.getRequestURI());
		} else {
			System.out.println("Not Authenticated!");
			path = notAuthenticatedURL;
		}
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
