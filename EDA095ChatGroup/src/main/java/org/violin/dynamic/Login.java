package org.violin.dynamic;

import java.io.IOException;

import org.violin.Handler;
import org.violin.database.Database;
import org.violin.database.generated.Message;

import com.sun.net.httpserver.HttpExchange;

public class Login extends Handler implements Action {

	public Login(Database db) {
		super(db);
	}

	@Override
	public void handle(HttpExchange arg0) throws IOException {
		
	}

	@Override
	public void perform(Message msg, HttpExchange exchange) {
		
	}

}
