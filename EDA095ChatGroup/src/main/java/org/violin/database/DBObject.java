package org.violin.database;

import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.management.Attribute;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

/**
 * @author Hampus Balldin
 * @param <T>
 */
public abstract class DBObject<T> {
	protected Database db;

	public DBObject(Database db) {
		this.db = db;
	}

	protected abstract Class<?> factoryType();

	protected abstract Class<?> rowType();

	protected abstract Class<T> rootType();

	protected abstract String rootName();

	protected abstract String rowName();

	protected abstract List<Attribute> primaryKey(Object o)
			throws ClassCastException;

	protected abstract List<Attribute> attributes(Object o)
			throws ClassCastException;

	protected abstract Iterator<?> iterator(T element);

	public T query(String sql) {
		T t = null;
		Connection conn = db.getConnection();
		try {
			PreparedStatement prep = conn.prepareStatement(sql);
			ResultSet rs = prep.executeQuery();
			Document doc = XMLUtilities.documentify(rs);
			t = unmarshal(doc);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			db.releaseConnection();
		}
		return t;
	}

	public T query(String sql, String... args) {
		T t = null;
		Connection conn = db.getConnection();

		try {
			PreparedStatement prep = conn.prepareStatement(sql);
			for (int i = 1; i <= args.length; i++) {
				prep.setString(i, args[i - 1]);
			}
			ResultSet rs = prep.executeQuery();
			Document doc = XMLUtilities.documentify(rs);
			t = unmarshal(doc);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} finally {
			db.releaseConnection();
		}
		return t;
	}

	public void marshal(T t, OutputStream out) {
		try {
			XMLUtilities.marshal(new JAXBElement<T>(new QName(t.getClass()
					.getCanonicalName(), t.getClass().getSimpleName()),
					rootType(), t), factoryType(), out);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public T unmarshal(Document doc) {
		T t = null;
		try {
			t = (T) XMLUtilities.unmarshal(doc, rootType(), factoryType(),
					rootName(), rowName());
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return t;
	}

	public void update(T element) {
		Iterator<?> itr = iterator(element);
		Connection conn = null;
		conn = db.getConnection();
		while (itr.hasNext()) {
			Object o = itr.next();
			update(o, conn);
		}
		db.releaseConnection();
	}

	public boolean insert(T element) {
		Iterator<?> itr = iterator(element);
		Connection conn = null;
		conn = db.getConnection();

		try {
			conn.setAutoCommit(false);
			while (itr.hasNext()) {
				Object o = itr.next();
				boolean success = insert(o, conn);
				if (!success) {
					conn.rollback();
					db.releaseConnection();
					return false;
				}
			}
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.releaseConnection();
		return true;
	}

	public String toString(Object row) {
		List<Attribute> attribs = attributes(row);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attribs.size(); i++) {
			Attribute attr = attribs.get(i);
			sb.append(rowName()).append("\n\t");
			sb.append(attr.getName()).append("=").append(attr.getValue())
					.append("', ");
		}
		String result = sb.toString().substring(0, sb.length() - 2);
		return result;
	}

	private boolean insert(Object row, Connection conn) {
		String columns = produceColumns(row);
		String values = produceValues(row);
		String sql = "INSERT INTO " + rootName() + "(" + columns + ")"
				+ " VALUES(" + values + ")";
		PreparedStatement prep = null;
		int success = 0;
		try {
			prep = conn.prepareStatement(sql);
			success = prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return success != 0;
	}

	private String produceColumns(Object row) {
		List<Attribute> attribs = attributes(row);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attribs.size(); i++) {
			Attribute attr = attribs.get(i);
			sb.append(attr.getName()).append(", ");
		}
		String result = sb.toString().substring(0, sb.length() - 2);
		return result;
	}

	private String produceValues(Object row) {
		List<Attribute> attribs = attributes(row);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < attribs.size(); i++) {
			Attribute attr = attribs.get(i);
			sb.append("'").append(attr.getValue()).append("', ");
		}
		String result = sb.toString().substring(0, sb.length() - 2);
		return result;
	}

	private void update(Object row, Connection conn) {
		String set = produceSet(row);
		String where = produceWhere(row);
		String sql = "UPDATE " + rootName() + " " + set + " " + where;
		PreparedStatement prep = null;
		try {
			prep = conn.prepareStatement(sql);
			prep.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String produceSet(Object row) {
		List<Attribute> attribs = attributes(row);
		attribs.removeAll(primaryKey(row));
		StringBuilder sb = new StringBuilder();
		sb.append("SET ");
		for (int i = 0; i < attribs.size(); i++) {
			Attribute attr = attribs.get(i);
			sb.append(attr.getName()).append("='").append(attr.getValue())
					.append("', ");
		}
		String result = sb.toString().substring(0, sb.length() - 2);
		return result;
	}

	private String produceWhere(Object row) {
		List<Attribute> prims = primaryKey(row);
		StringBuilder sb = new StringBuilder();
		sb.append("WHERE ");
		for (int i = 0; i < prims.size(); i++) {
			Attribute attr = prims.get(i);
			sb.append(attr.getName()).append("='").append(attr.getValue())
					.append("' AND ");
		}
		String result = sb.toString().substring(0, sb.length() - 5);
		return result;
	}

}
