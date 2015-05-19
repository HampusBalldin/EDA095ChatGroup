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
	private Queue<Message> msgs = new LinkedList<Message>();

	@Override
	public void run() {
		running = true;
		while (running) {
			HttpExchange exch = getExchange();
			try {
				exch.sendResponseHeaders(200, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			Message msg = getMessage();
			System.out.println("After Get Message: " + msg);
			if (running) {															//?
				QName qName = new QName("Message");
				JAXBElement<Message> jaxbElement = new JAXBElement<Message>(
						qName, Message.class, msg);
				try {
					XMLUtilities.marshal(jaxbElement, ObjectFactory.class,
							exch.getResponseBody());
				} catch (JAXBException e1) {
					e1.printStackTrace();	
				}
			}
		}
	}

	public void terminate() {										//?
		System.out.println("Inside receiver terminate");
		running = false;
		System.out.println("Notify Exchanges");
		exchanges.notifyAll();
		msgs.notifyAll();
	}
	

	public void addExchange(HttpExchange exchange) {
		synchronized (exchanges) {
			exchanges.add(exchange);
			exchanges.notify();
		}
	}
	
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

	public void addMessage(Message msg) {
		synchronized (msgs) {
			msgs.add(msg);
			msgs.notify();							//?
		}
	}

	public Message getMessage() {
		synchronized (msgs) {			
			while (msgs.size() == 0 && running) {	//?
				try {
					msgs.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("getMessage: " + running);
			}
			return msgs.poll();
		}
	}

}
