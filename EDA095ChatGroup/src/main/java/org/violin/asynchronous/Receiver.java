package org.violin.asynchronous;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;
import com.sun.net.httpserver.HttpExchange;

public class Receiver implements Runnable {
	private boolean running;
	private Queue<HttpExchange> exchanges = new LinkedList<HttpExchange>();
	private Queue<Message> messageQueue = new LinkedList<Message>();

	@Override
	public void run() {
		running = true;
		while (running) {
			HttpExchange exch = getReplyExchange();
			try {
				exch.sendResponseHeaders(200, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Message msg = retrieveFromReplyQueue();
			System.out.println("After Get Message: " + msg);
			if (running) {
				QName qName = new QName("Message");
				JAXBElement<Message> jaxbElement = new JAXBElement<Message>(
						qName, Message.class, msg);
				try {
					XMLUtilities.marshal(jaxbElement, ObjectFactory.class,
							exch.getResponseBody());						//skriver vi till outputstream här?
				} catch (JAXBException e1) {
					e1.printStackTrace();	
				}
			}
		}
	}

	public void terminate() {
		running = false;								//	System.out.println("Inside receiver terminate");
		exchanges.notifyAll();							//	System.out.println("Notify Exchanges");
		messageQueue.notifyAll();
	}
	
	public void addReplyExchange(HttpExchange exchange) {
		synchronized (exchanges) {
			exchanges.add(exchange);
			exchanges.notify();
		}
	}
	
	public HttpExchange getReplyExchange() {
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

	public void addToReplyQueue(Message msg) {
		synchronized (messageQueue) {
			messageQueue.add(msg);
			messageQueue.notify();
		}
	}

	public Message retrieveFromReplyQueue() {
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

}
