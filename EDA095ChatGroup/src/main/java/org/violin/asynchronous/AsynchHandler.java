package org.violin.asynchronous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.annotation.XmlEnumValue;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.MessageType;
import org.violin.database.generated.ObjectFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AsynchHandler {
	/**
	 * The principal which this handler is responsible for...
	 */
	private AsynchContexts contexts;
	private Receiver receiver;
	private Sender sender;

	public AsynchHandler(AsynchContexts contexts) {
		this.contexts = contexts;
		Receiver receiver = new Receiver();
		Sender sender = new Sender();
		Thread t1 = new Thread(receiver);
		Thread t2 = new Thread(sender);
		t1.start();
		t2.start();
	}

	public void handle(HttpExchange exchange) throws IOException {
		Message msg = getMessage(exchange);
		switch (msg.getType()) {
		case LOGIN:
			break;
		case LOGOUT:
			break;
		case REQUEST_RECEIVE_DATA:
			receiver.addExchange(exchange);
			break;
		case REQUEST_SEND_DATA:
			break;
		}
	}

	private Message getMessage(HttpExchange exchange) {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				exchange.getRequestBody()));
		StringBuilder sb = new StringBuilder();
		try {
			while (in.ready()) {
				sb.append(in.readLine());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getMessage(sb.toString());
	}

	private Message getMessage(String jsonString) {
		JSONObject jsonObject = new JSONObject(jsonString);
		String xml = XML.toString(jsonObject);
		System.out.println(jsonString);
		System.out.println(xml);
		Message msg = null;
		try {
			msg = XMLUtilities.unmarshal(XMLUtilities.documentify(xml),
					Message.class, ObjectFactory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}
}
