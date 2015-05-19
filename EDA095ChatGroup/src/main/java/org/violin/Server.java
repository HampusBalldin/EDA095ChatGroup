package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.asynchronous.AsynchHandlerManager;
import org.violin.database.Database;

import com.sun.net.httpserver.HttpServer;

public class Server {

	private Database db;

	public Server(Database db) {
		this.db = db;
	}

	public void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		RootHandler rootHandler = new RootHandler();							//?
		AsyncHandlerManager contexts = new AsyncHandlerManager();

		server.createContext("/", rootHandler);
		server.createContext("/chat", new ChatpageStaticHandler(rootHandler));
		server.createContext("/javascripts", new JavascriptHandler());
		server.createContext("/login", new LoginHandler(db, contexts));
		server.createContext("/logout", new LogoutHandler(db, contexts));		//fix
		server.setExecutor(null);
		server.start();
		// server.createContext(arg0, arg1);
	}
}