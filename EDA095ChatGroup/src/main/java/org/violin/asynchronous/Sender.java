package org.violin.asynchronous;

import java.util.LinkedList;
import java.util.Queue;

import org.violin.database.DBUsers;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;

public class Sender implements Runnable {

	Database db;
	private AsyncHandlerManager manager;
	private boolean running;
	private Queue<Message> messageQueue;

	public Sender(Database db, AsyncHandlerManager manager) {
		this.db = db;
		this.manager = manager;
		messageQueue = new LinkedList<Message>();
	}

	@Override
	public void run() {
		running = true;
		
		while (running) {
							
			Message msg = retrieveFromMessageQueue();
			if(msg != null){
			setDestination(msg);
			distributeMessage(msg);
			}
		}
	}

	public void terminate() {
		
		running = false; 
		
		synchronized (messageQueue) {
			
			try{
				
				messageQueue.notifyAll();
				
			}catch(Exception e){
				
				e.printStackTrace();
			}
		}
	}

	public void addToMessageQueue(Message msg) {
		synchronized (messageQueue) {
			messageQueue.add(msg);
			messageQueue.notify();
		}
	}

	private Message retrieveFromMessageQueue() {
		
		synchronized (messageQueue) {
			while (messageQueue.size() == 0 && running) {
				try {
					messageQueue.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("getMessage: " + running);
			}
			return messageQueue.poll();
		}
	}

	private void setDestination(Message msg) {
		User origin = msg.getOrigin();
		DBUsers dbUsers = new DBUsers(db);
		Users onlineFriends = dbUsers.getOnlineFriends(origin);
		msg.setDestinations(onlineFriends);
	}

	private void distributeMessage(Message msg) {
		manager.distributeMessage(msg);
	}

}
