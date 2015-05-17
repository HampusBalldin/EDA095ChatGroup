package org.violin;

import java.io.IOException;

import org.violin.database.DBFriends;
import org.violin.database.Database;
import org.violin.database.generated.Friends;


public class Main {
	public static void main(String[] args) {
		
		System.out.println("------- Connecting to Database --------");
		Database db = new Database();
		db.openConnection("root", "password");

		DBFriends dbFriends = new DBFriends(db);
		Friends friends = dbFriends.query("SELECT * FROM Friends");
		dbFriends.marshal(friends, System.out);

		System.out.println("------- Starting Server --------");
		Server server = new Server();
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
