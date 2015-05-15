package org.violin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ChatStaticHandler implements HttpHandler {
	private RootHandler rootHandler;

	public ChatStaticHandler(RootHandler rootHandler) {
		this.rootHandler = rootHandler;
	}

	public void handle(HttpExchange exchange) throws IOException {

		//
		// OBS! NEED TO CHECK IF LOGGED IN!
		//

		if (isAuhtenticated()) {
			String path = System.getProperty("user.dir")
					+ "/src/main/resources" + exchange.getRequestURI();
			System.out.println(path);
			System.out.println(exchange.getRequestURI());

			FileInputStream in = new FileInputStream(new File(path));
			exchange.sendResponseHeaders(200, 0);
			OutputStream os = exchange.getResponseBody();
			int b = 0;

			while ((b = in.read()) != -1) {
				os.write(b);
			}
			in.close();
			os.flush();
			os.close();
		} else {
			rootHandler.handle(exchange);
		}
	}

	public boolean isAuhtenticated() {
		return true;
	}
}
