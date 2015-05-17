package org.violin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class StaticHandler implements HttpHandler {
	private MimetypesFileTypeMap mimeMap;

	public void handle(HttpExchange exchange) throws IOException {
		String path = System.getProperty("user.dir") + "/src/main/resources"
				+ exchange.getRequestURI();
		System.out.println(path);
		System.out.println(exchange.getRequestURI());
		String fileType = getFileType(exchange.getRequestURI());
		String mime = mimeMap
				.getContentType(exchange.getRequestURI().getPath());


		FileInputStream in = new FileInputStream(new File(path));
		exchange.sendResponseHeaders(200, 0);
		BufferedOutputStream os = new BufferedOutputStream(
				exchange.getResponseBody());
		int b = 0;
		while ((b = in.read()) != -1) {
			os.write(b);
		}
		os.flush();
		os.close();
		in.close();
	}

	private String getFileType(URI requestUri) {
		String[] splits = requestUri.getPath().split("\\.");
		if (splits.length >= 2) {
			return splits[1];
		}
		return "";
	}
}
