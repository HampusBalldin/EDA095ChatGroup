package org.violin;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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
	
	
	private class MimeResolver {
		
	}
}
