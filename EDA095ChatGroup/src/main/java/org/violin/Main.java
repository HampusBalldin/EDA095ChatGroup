package org.violin;

import java.io.IOException;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.database.DBFriends;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Friends;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.Status;

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

		Status s = Status.ONLINE;
		QName qName = new QName("Status");
		JAXBElement<Status> jaxb = new JAXBElement<Status>(qName, Status.class,
				s);
		try {
			XMLUtilities.marshal(jaxb, ObjectFactory.class, System.out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
