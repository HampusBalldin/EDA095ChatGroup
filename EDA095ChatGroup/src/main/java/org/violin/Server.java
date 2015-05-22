package org.violin;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.Database;
import org.violin.dynamic.DynamicHandler;

import com.sun.net.httpserver.HttpServer;

public class Server {
	private Database db;

	public Server(Database db) {
		this.db = db;
	}

	public void start() throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		RootHandler rootHandler = new RootHandler();
		AsyncHandlerManager contexts = new AsyncHandlerManager(db, server);

		server.createContext("/", rootHandler);
		server.createContext("/chat", new StaticHandler(db));
		server.createContext("/javascripts", new StaticHandler(db));
		// server.createContext("/login", new StaticHandler());
		server.createContext("/loginhandler", new DynamicHandler(db, contexts));
		server.createContext("/getfriends", new DynamicHandler(db, contexts));
		server.createContext("/logouthandler", new DynamicHandler(db, contexts));
		server.createContext("/dynamichandler",
				new DynamicHandler(db, contexts));
		StaticLoginHandler staticLoginHandler = new StaticLoginHandler();
		
		// Special Case Login
		server.createContext("/login/", staticLoginHandler);

		server.setExecutor(null);
		server.start();
		// server.createContext(arg0, arg1);
	}
}