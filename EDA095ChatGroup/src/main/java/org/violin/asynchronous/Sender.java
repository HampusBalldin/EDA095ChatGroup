package org.violin.asynchronous;

import java.util.ArrayList;

import org.violin.database.Database;
import org.violin.database.generated.Friend;
import org.violin.database.generated.Friends;
import org.violin.database.generated.Message;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

import com.sun.net.httpserver.HttpExchange;

/**
 * Request to send is handled here. The destination for a message is assumed to
 * be all friends which are not online.
 * 
 * @author Hampus Balldin
 */

//ska ha tillgång till databasen
// kolla med databasen vilka av mina vänner som är online
// genererar ett message med receivers setDestinations(Users value) med receivers


public class Sender implements Runnable {
	
	Database db;
	
	public Sender (Database db) {
		this.db = db;

	}
	
	@Override
	public void run() {			//så länge kön är tom så väntar den, 
								//vid innehåll, skapa meddelande och distribuera
		
		
		//svara klienten (kolla i static handler) skicka headers 
		//send responseheader(200, ok);
	
		@Override
		 public void run() { // fix
		  while(true){
		   HttpExchange exchange = getExchange();
		   createMessage(exchange);
		   
		  }

		
		
		
		private Queue<HttpExchange> exchanges = new LinkedList<HttpExchange>();
		public HttpExchange getExchange() {
		  synchronized (exchanges) {
		   while (exchanges.size() == 0 && running) {
		    try {
		     exchanges.wait();
		    } catch (InterruptedException e) {
		     e.printStackTrace();
		    }
		    System.out.println("getExchange: " + running);
		   }
		   return exchanges.poll();
		  }
		 }

		 public void addExchange(HttpExchange exchange) {
		  synchronized (exchanges) {
		   exchanges.add(exchange);
		   exchanges.notify();
		  }
		 }
		
	}

	public void terminate() {	//fix
	}
	
	public void createMessage(HttpExchange exchange) {
		Message msg = new Message();
		Users users = findFriendsOnline();
		msg.setDestinations(users);
		msg.setData(exchange.getbo);			//fix
		asynchmanager.distributemessage(name)	//fix
	}
	

	private Users findFriendsOnline() {
		Users users1 = dbUsers.query(SELECT uid_2 FROM Users, Friends WHERE uid_1 = ? AND uid = uid_2 AND status = online );
		Users users2 = dbUsers.query(SELECT uid_1 FROM Users, Friends WHERE uid_2 = ArrayList<E> = uid_1 AND status = online );
		Users users = new Users();
		for (User user : users1) {
			users.add(user);			//skapa metod addUser() i "Users"
		}
		for (User user : users2) {
			users.add(user);
		}
		return users;
	}
	
	
	public void notifyOnlineFriends(User user) {
		DBUsers dbUsers = new DBUsers(db);

		Users users = dbUsers.query("SELECT * " + " FROM Users"
				+ " WHERE (status = ? OR status = ?) AND (uid IN"
					+ " (SELECT uid_1 FROM friends"
				+ " WHERE ? = uid_2) OR uid IN " + " (SELECT uid_2"
				+ " FROM friends " + " WHERE ? = uid_1))",
				Status.ONLINE.value(), Status.AWAY.value(), "" + user.getUid(),
				"" + user.getUid());

		for (User friend : users.getUser()) {
			System.out.println(friend.getUid() + " - " + friend.getStatus());
		}
	}
}
}
