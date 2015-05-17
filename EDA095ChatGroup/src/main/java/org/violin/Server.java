package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.violin.asynchronous.LoginHandler;
import org.violin.database.Database;

import com.sun.net.httpserver.HttpServer;

public class Server {

	private Database db;

	public Server(Database db) {
		this.db = db;
	}

	public void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		RootHandler rootHandler = new RootHandler();

		server.createContext("/", rootHandler);
		server.createContext("/chat", new ChatpageStaticHandler(rootHandler));
		server.createContext("/javascripts", new JavascriptHandler());
		server.createContext("/async/login", new LoginHandler(db, server));
		server.setExecutor(null);
		server.start();

		// server.createContext(arg0, arg1);
	}
}