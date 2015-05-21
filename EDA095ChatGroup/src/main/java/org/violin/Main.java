package org.violin;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import org.violin.database.DBFriends;
import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Friend;
import org.violin.database.generated.Friends;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;
import org.violin.database.generated.Users;

public class Main {
	public static void main(String[] args) {
		System.out.println("------- Connecting to Database --------");
		Database db = new Database();
		db.openConnection("eda095user", "bestpassword");

		DBFriends dbFriends = new DBFriends(db);
		Friends friends = dbFriends.query("SELECT * FROM Friends");
		// dbFriends.marshal(friends, System.out);
		for (Friend f : friends.getFriend()) {
			String uid_1 = f.getUid1();
			String uid_2 = f.getUid2();
			System.out.println(uid_1 + " is friends with " + uid_2);
		}

		System.out.println("------- Starting Server --------");
		Server server = new Server(db);
		try {
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Status s = Status.ONLINE;
		QName qName = new QName("Status");
		JAXBElement<Status> jaxb = new JAXBElement<Status>(qName, Status.class,
				s);
		try {
			XMLUtilities.marshal(jaxb, ObjectFactory.class, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		DBUsers dbUsers = new DBUsers(db);
		Users users = dbUsers.query("SELECT * FROM Users");

		qName = new QName("Users");
		JAXBElement<Users> jaxbUsers = new JAXBElement<Users>(qName,
				Users.class, users);
		try {
			XMLUtilities.marshal(jaxbUsers, ObjectFactory.class, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}
}
