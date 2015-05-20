package org.violin;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.HTTPUtilities.MimeResolver;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class LoginHandler implements com.sun.net.httpserver.HttpHandler {
	private Database db;
	private AsyncHandlerManager manager;
	private DBUsers dbUsers;
	private ChatpageStaticHandler chatpageStaticHandler;

	public LoginHandler(Database db, AsyncHandlerManager manager,
			ChatpageStaticHandler chatpageStaticHandler) {
		super();
		this.db = db;
		this.manager = manager;
		dbUsers = new DBUsers(db);
		this.chatpageStaticHandler = chatpageStaticHandler;
	}

	@Override
	public void handle(HttpExchange exchange) {
		User user = getUser(exchange.getRequestBody());
		System.out.println(user.getUid() + user.getPwd()
				+ user.getStatus().value());
		dbLogin(user);
		createContext(user);
		setCookie(user, exchange);
		HTTPUtilities.printHeaders(exchange.getResponseHeaders());
		Users users = dbUsers.getOnlineFriends(user);
		System.out.println("Found online friends for  " + user.getUid() + user.getPwd() + user.getStatus().value());
		Iterator<User> itr = users.getUser().iterator();
		while (itr.hasNext()) {
			User u = itr.next();
			System.out.println(u.getUid() + u.getPwd() + u.getStatus().value());
		}

		try {
			String path = System.getProperty("user.dir")
					+ "/src/main/resources/chat/index.html";
			send(exchange, users, path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void send(HttpExchange exchange, Users users, String path)
			throws IOException {
		MimeResolver resolver = new MimeResolver();
		System.out.println(path);
		System.out.println(exchange.getRequestURI());
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

		QName qName = new QName("Users");
		JAXBElement<Users> jaxb = new JAXBElement<Users>(qName, Users.class,
				users);
		try {
			XMLUtilities.marshal(jaxb, ObjectFactory.class, os);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		os.flush();
		os.close();
		in.close();
	}

	public User getUser(InputStream in) {
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String read = "";
		try {
			while (br.ready()) {
				read += br.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] info = read.split("&");
		String uid = info[0].split("=")[1];
		String pwd = info[1].split("=")[1];
		return dbUsers.createUser(uid, pwd, Status.ONLINE);
	}

	private void dbLogin(User user) {
		dbUsers.update(user);
	}

	private void createContext(User user) {
		manager.createContext(user);
	}

	private void setCookie(User user, HttpExchange exchange) {
		Headers headers = exchange.getResponseHeaders();
		headers.set("Cookie",
				"uid=" + user.getUid() + ";" + "pwd=" + user.getPwd());
	}
}
