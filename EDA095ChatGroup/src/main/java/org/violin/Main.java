package org.violin;

import java.io.IOException;

import org.violin.database.Database;

public class Main {
	public static void main(String[] args) {
		System.out.println("------- Connecting to Database --------");
		Database db = new Database();
		boolean connected = db.openConnection("eda095user", "bestpassword");
		if (!connected) {
			System.err.println("Connection to database failed, exiting...");
			System.exit(0);
		}
		System.out
				.println("------- Succesfully Connected to Database --------");
		System.out.println("------- Starting Server --------");
		Server server = new Server(db);
		try {
			server.start();
			System.out.println("------- Server Started --------");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
