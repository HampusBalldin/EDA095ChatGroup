package org.violin.dynamic;

import java.util.HashMap;

import org.violin.HTTPUtilities;
import org.violin.Handler;
import org.violin.asynchronous.AsyncHandlerManager;
import org.violin.database.Database;
import org.violin.database.generated.Message;
import org.violin.database.generated.MessageType;

import com.sun.net.httpserver.HttpExchange;

public class DynamicHandler extends Handler {
	private Login login;
	private CheckConnection checkConnection;
	private Logout logout;
	private GetFriends getFriends;

	private HashMap<MessageType, Action> actions = new HashMap<MessageType, Action>();

	public DynamicHandler(Database db, AsyncHandlerManager manager) {
		super(db);
		login = new Login(db, this, manager);
		logout = new Logout(db, manager);
		checkConnection = new CheckConnection(manager);
		getFriends = new GetFriends(db);

		actions.put(MessageType.LOGIN, login);
		actions.put(MessageType.LOGOUT, logout);
		actions.put(MessageType.CHECK_CONNECTION_STATUS, checkConnection);
		actions.put(MessageType.GET_FRIENDS, getFriends);
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("Dynamic Handler");
		HTTPUtilities.printHeaders(exchange.getRequestHeaders());
		String exchangeContent = getExchangeContent(exchange);
		Message msg = createMessage(exchangeContent);
		if (msg != null) {
			actions.get(msg.getType()).perform(msg, exchange);
		} else {
			System.out
					.println("DynamicHandler: Message is null,  Cannot Handle!");
		}
		exchange.close();
	}
}
