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
	public void run() {			//fix
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
	
}
