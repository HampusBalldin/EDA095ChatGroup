package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.violin.asynchronous.LoginHandler;

import com.sun.net.httpserver.HttpServer;

public class Server {

	public void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		RootHandler rootHandler = new RootHandler();

		server.createContext("/", rootHandler);
		server.createContext("/chat", new ChatpageStaticHandler(rootHandler));
		server.createContext("/javascripts", new JavascriptHandler());
		server.createContext("/asynchronous/login/", new LoginHandler(server));

		server.setExecutor(null);
		server.start();
		// server.createContext(arg0, arg1);
	}
}