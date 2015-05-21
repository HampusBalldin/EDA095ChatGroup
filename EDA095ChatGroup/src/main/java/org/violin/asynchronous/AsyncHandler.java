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
		System.out.println("ASYNCH HANDLER");
		String exchangeContent = getExchangeContent(exchange);

		System.out.println("START");
		System.out.println(exchangeContent);
		System.out.println("END");
		Message msg = createMessage(exchangeContent);
		System.out.println("Was Able to Parse Message :)!");
		System.out.println(msg.getOrigin());
//		if (authenticate(msg.getOrigin())) {
			System.out.println("ASYNCHANDLER AUTHENTICATED");
			System.out.println(msg.getType());
			switch (msg.getType()) {
			case REQUEST_RECEIVE_DATA:
				System.out.println("RECEIVER");
				receiver.addReplyExchange(exchange);
				break;
			case REQUEST_SEND_DATA:
				System.out.println("SENDER");
				exchange.sendResponseHeaders(200, 0);
				sender.addToMessageQueue(msg);
				break;
			}
//		}else{
//			System.out.println("ASYNCHANDLER NOT AUTHENTICATED");
//		}
	}

	public void receiveMessage(Message msg) {
		receiver.addToReplyQueue(msg);
	}

}
