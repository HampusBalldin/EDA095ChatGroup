package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpServer;

public class Server {
	public void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		RootHandler rootHandler = new RootHandler();
		server.createContext("/", rootHandler);
		server.createContext("/chat", new ChatStaticHandler(rootHandler));
		server.setExecutor(null);
		server.start();
	}
}