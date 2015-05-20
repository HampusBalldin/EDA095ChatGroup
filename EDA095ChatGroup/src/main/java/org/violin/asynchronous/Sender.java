package org.violin.asynchronous;

import java.util.LinkedList;
import java.util.Queue;

import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;


public class Sender implements Runnable {
	
	Database db;
	private AsyncHandlerManager manager;
	private boolean running;
	private Queue<Message> messageQueue;
	
	public Sender (Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;
		messageQueue = new LinkedList<Message>();
	}
		
	@Override												//efterlikna run i Receiver
	 public void run() {
		running = true;
		while(running) {
			Message msg = retrieveFromMessageQueue();
			setDestination(msg);
			distributeMessage(msg);
		}
	}
	
	public void terminate() {
		running = false;
		messageQueue.notifyAll();
	}

	public void addToMessageQueue(Message msg) {			//synchronized (exchanges) 
		messageQueue.add(msg);
		messageQueue.notify();								//?
	}
	
	private Message retrieveFromMessageQueue() {					//synchronized (exchanges) 
		while (messageQueue.size() == 0 && running) {		//running?
			try {
				messageQueue.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("getMessage: " + running);
		}
		return messageQueue.poll();
	}
	
	private void setDestination(Message msg) {
		User origin = msg.getOrigin();
		Users onlineFriends = getOnlineFriends(origin);
		msg.setDestinations(onlineFriends);
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
