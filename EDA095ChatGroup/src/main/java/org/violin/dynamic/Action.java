package org.violin.dynamic;

import org.violin.database.generated.Message;

import com.sun.net.httpserver.HttpExchange;

public interface Action {
	void perform(Message msg, HttpExchange exchange);
}
