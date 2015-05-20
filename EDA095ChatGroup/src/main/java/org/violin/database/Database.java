package org.violin.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private Connection conn = null;
	private boolean isHeld = false;

	public boolean openConnection(String user, String pass) {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1/EDA095",
					user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		synchronized (conn) {
			while (isHeld) {
				try {
					conn.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			isHeld = true;
			return conn;
		}
	}

	public void releaseConnection() {
		synchronized (conn) {
			isHeld = false;
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			conn.notify();
		}
	}

	public void reset() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(System.getProperty("user.dir")
							+ "\\src\\main\\resources\\create_database.txt"))));
			Connection conn = getConnection();
			StringBuilder sb = new StringBuilder();
			while (reader.ready()) {
				sb.append(reader.readLine());
			}
			String[] sqls = sb.toString().split(";");
			for (int i = 0; i < sqls.length; i++) {
				Statement stmnt = conn.createStatement();
				String query = sqls[i];
				stmnt.executeUpdate(query);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		releaseConnection();
	}
}
