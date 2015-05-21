package org.violin.dynamic;

import org.violin.Handler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
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
		User user = createUser(exchange);
		switch (msg.getType()) {
		case LOGIN: // kan vi få LOGIN i annat fall än i det första? ok!
			dbLogin(user); // 1. loggar in i databaen
			createContext(user); // 2. skapar context
			setCookie(user, exchange); // 3. sätter cookie

		case LOGOUT:
			dbLogout(user); // 1. loggar ut ur databaen
			removeContext(user); // 2. ta bort context
			setCookie(user, exchange); // 3. ta bort cookie / sätt
										// cookie-status?

		case GET_FRIENDS: // skicka users
			Users users = getFriends(user);
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
