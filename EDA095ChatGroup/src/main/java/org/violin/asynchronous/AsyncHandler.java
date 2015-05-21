package org.violin.asynchronous;

import java.io.IOException;

import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.User;

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
		User user = createUser(exchange);
		if (authenticate(user)) {
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

	
}
