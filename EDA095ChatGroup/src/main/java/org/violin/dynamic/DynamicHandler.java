package org.violin.dynamic;

import java.io.OutputStream;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.Handler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class DynamicHandler extends Handler {
	private Database db;
	private AsyncHandlerManager manager;
	private DBUsers dbUsers;

	public DynamicHandler(Database db, AsyncHandlerManager manager) {
		super();
		this.db = db;
		this.manager = manager;
		dbUsers = new DBUsers(db);
	}

	@Override
	public void handle(HttpExchange exchange) {
		String exchangeContent = getExchangeContent(exchange);
		Message msg = createMessage(exchangeContent);
		User user;
		switch(msg.getType()) {
		case LOGIN:
			user = msg.getOrigin();
			if (dbUsers.authenticate(user)) {
				dbLogin(user); //loggar in i databasen
				createContext(user); //skapar context
				setCookie(user, exchange); //sätter cookie
				break;
			} else {
				break;
			}
		case LOGOUT:
			user = createUser(exchange); 
			dbLogout(user); //loggar ut ur databaen
			removeContext(user); //tar bort context
			break;
		case GET_FRIENDS: //skicka users
			user = createUser(exchange);
			Users users = getFriends(user); 
			OutputStream os = exchange.getResponseBody();
			sendFriends(os,users); //gör till xml-sträng, skickar strängen
			break;
		}	
	}

	private void dbLogin(User user) {
		user.setStatus(Status.ONLINE);
		dbUsers.update(user);
	}

	private void dbLogout(User user) {
		user.setStatus(Status.OFFLINE);
		dbUsers.update(user);
	}

	private void createContext(User user) {
		manager.createContext(user);
	}

	private void removeContext(User user) {
		manager.removeContext(user);
	}

	private void setCookie(User user, HttpExchange exchange) {
		Headers headers = exchange.getResponseHeaders();
		headers.set("Cookie",
				"uid=" + user.getUid() + ";" + "pwd=" + user.getPwd());
	}

	private Users getFriends(User user) {
		return dbUsers.getFriends(user);
	}

	private Users getOnlineFriends(User user) {
		return dbUsers.getOnlineFriends(user);
	}
	
	private void sendFriends(OutputStream os, Users users) {
		QName qName = new QName("Users");
		JAXBElement<Users> jaxbElement = new JAXBElement<Users>(qName, Users.class, users);
		try {
			XMLUtilities.marshal(jaxbElement, ObjectFactory.class, os);
		} catch (JAXBException e) {
			e.printStackTrace();	
		}	
	}

	// if (authenticate(exchange)) { //implementera authenticate
	// User user = getUser(exchange.getRequestBody());
	// System.out.println(user.getUid() + user.getPwd()
	// + user.getStatus().value());
	// dbLogin(user);
	// createContext(user);
	// setCookie(user, exchange);
	// HTTPUtilities.printHeaders(exchange.getResponseHeaders());
	// Users users = dbUsers.getOnlineFriends(user);
	// System.out.println("Found online friends for  " + user.getUid()
	// + user.getPwd() + user.getStatus().value());
	// if (users != null) {
	// Iterator<User> itr = users.getUser().iterator();
	// while (itr.hasNext()) {
	// User u = itr.next();
	// System.out.println(u.getUid() + u.getPwd()
	// + u.getStatus().value());
	// }
	// }
	//
	// try {
	// String path = System.getProperty("user.dir")
	// + "/src/main/resources/chat/index.html";
	// send(exchange, users, path);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// public User getUser(InputStream in) {
	// BufferedReader br = new BufferedReader(new InputStreamReader(in));
	// String read = "";
	// try {
	// while (br.ready()) {
	// read += br.readLine();
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// String[] info = read.split("&");
	// String uid = info[0].split("=")[1];
	// String pwd = info[1].split("=")[1];
	// return dbUsers.createUser(uid, pwd, Status.ONLINE);
	// }
	//
	// @Override
	// public void handle(HttpExchange exchange) {
	// //System.out.println("LoginHandler: " + exchange.getRequestURI());
	// String query = exchange.getRequestURI().getQuery();
	// String uid = ""; //utvinn uid ur query
	// String pwd = ""; //utvinn pwd ur query
	// Status status = Status.OFFLINE;
	// User user = new User();
	// user.setUid(uid);
	// user.setPwd(pwd);
	// user.setStatus(status);
	// dbLogout(user);
	// removeContext(user);
	// }

}
