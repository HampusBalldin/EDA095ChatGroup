package org.violin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.violin.HTTPUtilities.MimeResolver;
import com.sun.net.httpserver.HttpExchange;

public class StaticLoginHandler implements com.sun.net.httpserver.HttpHandler {
	private static final MimeResolver resolver = new MimeResolver();

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		System.out.println("StaticLoginHandler" + exchange.getRequestURI());
		String path = System.getProperty("user.dir") + "/src/main/resources"
				+ exchange.getRequestURI();
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
		os.close();
		in.close();
	}
}
