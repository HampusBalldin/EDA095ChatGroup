package org.violin.asynchronous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.Database;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;

import com.sun.net.httpserver.HttpExchange;

public class AsyncHandler extends org.violin.Handler {

	private Receiver receiver;
	private Sender sender;

	public AsyncHandler(Database db, AsyncHandlerManager manager) {
		receiver = new Receiver();
		sender = new Sender(db, manager);
	}

	public void start() {
		Thread receiverThread = new Thread(receiver);
		Thread senderThread = new Thread(sender);
		receiverThread.start();
		senderThread.start();
	}

	public void terminate() {
		System.out.println("Call Receiver Terminate");
		receiver.terminate();
		System.out.println("Call Sender Terminate");
		sender.terminate();
	}

	public void handle(HttpExchange exchange) throws IOException {
		if (authenticate(exchange)) {
			String exchangeContent = getExchangeContent(exchange);
			Message msg = createMessage(exchangeContent);
			switch (msg.getType()) {
			case REQUEST_RECEIVE_DATA:
				receiver.addReplyExchange(exchange);
				break;
			case REQUEST_SEND_DATA:
				exchange.sendResponseHeaders(200, 0);
				sender.addToMessageQueue(msg);
				break;
			}
		}
	}

	public void receiveMessage(Message msg) {
		receiver.addToReplyQueue(msg);
	}

	private String getExchangeContent(HttpExchange exchange) {
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
		return sb.toString();
	}

	private Message createMessage(String jsonString) {
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
