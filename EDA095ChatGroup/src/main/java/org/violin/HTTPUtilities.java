package org.violin;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.violin.database.Database;
import org.violin.database.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.net.httpserver.Headers;

public class HTTPUtilities {

	public HTTPUtilities() {

	}

	public static void printHeaders(Headers h) {
		Iterator<Entry<String, List<String>>> itr = h.entrySet().iterator();

		while (itr.hasNext()) {
			Entry<String, List<String>> entry = itr.next();
			String key = entry.getKey();
			List<String> values = entry.getValue();
			StringBuilder sb = new StringBuilder();
			sb.append(key).append(": ");
			Iterator<String> itr2 = values.iterator();

			while (itr2.hasNext()) {
				String value = itr2.next();
				sb.append(value);
			}
			System.out.println(sb.toString());
		}
	}

	public static class MimeResolver {
		private HashMap<String, String> mimes = new HashMap<String, String>();

		public String resolveHttpContent(URI requestUri) {
			String type = getFileType(requestUri);
			if (mimes.get(type) == null) {
				String path = System.getProperty("user.dir")
						+ "/src/main/resources/mimetypes.xml";
				try {
					Document document = XML.getDocument(new FileInputStream(
							new File(path)));
					findEntries(document.getDocumentElement());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return mimes.get(type);
		}

		public String getFileType(URI requestUri) {
			String[] splits = requestUri.getPath().split("\\.");
			if (splits.length >= 2) {
				return splits[1];
			}
			return "";
		}

		private void findEntries(Node node) {
			if (node.getNodeName().equals("mime")) {
				handleEntry(node);
			} else {
				NodeList nodeList = node.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node currentNode = nodeList.item(i);
					if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
						findEntries(currentNode);
					}
				}
			}
		}

		private void handleEntry(Node node) {
			NodeList nodeList = node.getChildNodes();
			String fileEnd = "";
			String mime = "";
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node n = nodeList.item(i);
				if (n.getNodeName().equals("file-ending")) {
					fileEnd = n.getTextContent().trim();
				}
				if (n.getNodeName().equals("content-type")) {
					mime = n.getTextContent().trim();
				}
			}
			mimes.put(fileEnd, mime);
		}
	}
}
