package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.violin.asynchronous.AsynchContexts;
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
		AsynchContexts asynchContexts = new AsynchContexts(db);

		server.createContext("/", rootHandler);
		server.createContext("/chat", new ChatpageStaticHandler(rootHandler));
		server.createContext("/javascripts", new JavascriptHandler());
		server.createContext("/chat/login",
				new LoginHandler(db, asynchContexts));
		server.createContext("/async", asynchContexts);
		server.setExecutor(null);
		server.start();
		// server.createContext(arg0, arg1);
	}
}