package org.violin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.violin.database.DBUsers;
import org.violin.database.generated.Status;
import org.violin.database.generated.User;

import com.sun.net.httpserver.Headers;

public class Cookies {
	public void setCookie(User user, Headers headers) {
		List<String> values = new ArrayList<String>();
		String expires = daysFromNow(5).toString();
		values.add("uid=" + user.getUid() + "; expires=" + expires + ";path=/;");
		values.add("pwd=" + user.getPwd() + "; expires=" + expires + ";path=/;");
		headers.put("Set-Cookie", values);
		headers.set("Access-Control-Max-Age", "360");
		headers.set("Access-Control-Allow-Credentials", "true");
		headers.set("Access-Control-Allow-Headers", "Authorization");
	}

	public Date daysFromNow(int days) {
		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, 5);
		return c.getTime();
	}

	public String getCookie(String cName, String cookies) {
		String value = "";
		Pattern p = Pattern.compile(cName + "=[^;]*");
		Matcher m = p.matcher(cookies);
		if (m.find()) {
			value = m.group(0).split("=")[1];
		}
		System.out.println("In getCookie, " + cName + " = [" + value
				+ "], looked in cookies: " + cookies);
		return value;
	}

	public void deleteCookies(User user, Headers headers) {
		List<String> values = new ArrayList<String>();
		values.add("uid="
				+ user.getUid()
				+ "; token=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
		values.add("pwd="
				+ user.getPwd()
				+ "; token=deleted; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT");
		headers.put("Set-Cookie", values);
		headers.set("Access-Control-Max-Age", "360");
		headers.set("Access-Control-Allow-Credentials", "true");
		headers.set("Access-Control-Allow-Headers", "Authorization");
	}

	/**
	 * Extracts user data from cookies.
	 * 
	 * @param reqHeaders
	 * @param dbUsers
	 * @return
	 * @throws NullPointerException
	 */
	public User extractUserFromCookies(Headers reqHeaders, DBUsers dbUsers)
			throws NullPointerException {
		List<String> tmp= reqHeaders.get("Cookie");
		String cookies = "";
		if(tmp != null){
			cookies = tmp.get(0);
		}
		if (!"".equals(cookies)) {
			return dbUsers.createUser(getCookie("uid", cookies),
					getCookie("pwd", cookies), Status.ONLINE);
		}
		throw new NullPointerException();
	}
}
