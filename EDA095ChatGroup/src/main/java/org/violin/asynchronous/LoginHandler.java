package org.violin.asynchronous;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONObject;
import org.json.XML;
import org.violin.database.XMLUtilities;
import org.violin.database.generated.ObjectFactory;
import org.violin.database.generated.User;
import org.violin.database.generated.Users;
import org.xml.sax.SAXException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class LoginHandler extends AsyncHandler {

	public LoginHandler(HttpServer server) {
		super(server);
	}

	@Override
	public void handle(HttpExchange exchange) {
		System.out.println("LoginHandler: " + exchange.getRequestURI());
		switch (exchange.getRequestMethod().toUpperCase()) {
		case "GET":
			handleGet(exchange);
			break;
		case "POST":
			handlePost(exchange);
			break;
		}
	}

	public void handleGet(HttpExchange exchange) {

	}

	public void handlePost(HttpExchange exchange) {
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
		JSONObject jsonObject = new JSONObject(sb.toString());
		System.out.println(sb.toString());
		System.out.println(XML.toString(jsonObject));

		String xml = XML.toString(jsonObject);

		try {
			System.out.println("1");
			Users users = XMLUtilities.unmarshal(XMLUtilities.documentify(xml),
					Users.class, ObjectFactory.class, "Users", "User");

			User user = users.getUser().get(0);
			System.out.println("uid = " + user.getUid() + ", pwd = "
					+ user.getPwd() + "status= " + user.getStatus());
			
		} catch (JAXBException | ParserConfigurationException | SAXException
				| IOException e) {
			e.printStackTrace();
		}
		// rowName)

	}
}
