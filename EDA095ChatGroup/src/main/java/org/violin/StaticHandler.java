package org.violin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.violin.HTTPUtilities.MimeResolver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StaticHandler implements HttpHandler {
	private static final MimeResolver resolver = new MimeResolver();

	public void handle(HttpExchange exchange) throws IOException {
		String path = System.getProperty("user.dir") + "/src/main/resources"
				+ exchange.getRequestURI();
		System.out.println(path);
		System.out.println(exchange.getRequestURI());
		exchange.getResponseHeaders().set("Content-Type",
				resolver.resolveHttpContent(exchange.getRequestURI()));
		FileInputStream in = new FileInputStream(new File(path));
		exchange.sendResponseHeaders(200, 0);
		BufferedOutputStream os = new BufferedOutputStream(
				exchange.getResponseBody());
		int b = 0;
		while ((b = in.read()) != -1) {
			os.write(b);
		}
		os.flush();
		os.close();
		in.close();
	}
	
}
