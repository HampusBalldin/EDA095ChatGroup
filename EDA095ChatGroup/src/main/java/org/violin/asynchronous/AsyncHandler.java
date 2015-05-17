package org.violin.asynchronous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.Message;
import org.violin.database.generated.ObjectFactory;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class AsyncHandler implements HttpHandler {
	/**
	 * The principal which this handler is responsible for...
	 */
	private String principal;
	protected HttpServer server;

	public AsyncHandler(HttpServer server) {
		this.server = server;
	}

	public void handle(HttpExchange exchange) throws IOException {
		Headers requestHeaders = exchange.getRequestHeaders();
	}

	public String getPrincipal() {
		return principal;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AsyncHandler))
			return false;
		return principal.equals(((AsyncHandler) obj).principal);
	}

	protected Message getMessage(HttpExchange exchange) {
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
