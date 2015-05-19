package org.violin.asynchronous;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Friend;
import org.violin.database.generated.Friends;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.HttpExchange;

//ska ha tillgång till databasen
//kolla med databasen vilka av mina vänner som är online
//genererar ett message med receivers setDestinations(Users value) med receivers
//så länge kön är tom så väntar den, vid innehåll skapa meddelande och distribuera
//svara klienten (kolla i static handler) skicka headers, send responseheader(200, ok);

public class Sender implements Runnable {
	
	Database db;
	private AsyncHandlerManager manager;
	private Queue<HttpExchange> exchanges;
	
	public Sender (Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;
		exchanges = new LinkedList<HttpExchange>();
	}
		
	@Override
	 public void run() {
		while(true) {
			HttpExchange exchange = getExchange();
			Message msg = createMessage(exchange);
			distributeMessage(msg);
		}
	}
	
	public void terminate() {							//fix
	}

	public void addExchange(HttpExchange exchange) {	//synchronized (exchanges) 
		exchanges.add(exchange);
		exchanges.notify();								//?
	}
	
	private HttpExchange getExchange() {				//synchronized (exchanges) 
		while (exchanges.size() == 0 && running) {		//running?
			try {
				exchanges.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("getExchange: " + running);
		}
		return exchanges.poll();
	}
	
	private Message createMessage(HttpExchange exchange) {
		String origin = "";								//fix
		User user = new User();
		user.setUid(origin);							//räcker uid?
		Users onlineFriends = getOnlineFriends(user);
		String data = "";
		Message msg = new Message();
		msg.setOrigin(user);
		msg.setDestinations(onlineFriends);
		msg.setData(data);
		return msg;
	}
	
	private void distributeMessage(Message msg) {
		manager.distributeMessage(msg);
	}	
		
	private Users getOnlineFriends(User user) {
		DBUsers dbUsers = new DBUsers(db);
		Users users = dbUsers.query("SELECT * " + " FROM Users"
				+ " WHERE (status = ? OR status = ?) AND (uid IN"
					+ " (SELECT uid_1 FROM friends"
				+ " WHERE ? = uid_2) OR uid IN " + " (SELECT uid_2"
				+ " FROM friends " + " WHERE ? = uid_1))",
				Status.ONLINE.value(), Status.AWAY.value(), "" + user.getUid(),
				"" + user.getUid());
		return users;

	}

}
