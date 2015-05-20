package org.violin.database;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLUtilities {
	public static String stringify(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static <T1, T2> T1 unmarshal(ResultSet rs, Class<T1> type,
			Class<T2> jaxbFactory, String rootName, String rowName)
			throws JAXBException, ParserConfigurationException, SQLException {
		Document doc = XMLUtilities.documentify(rs);
		return unmarshal(doc, type, jaxbFactory, rootName, rowName);
	}

	public static <T1, T2> T1 unmarshal(Document doc, Class<T1> type,
			Class<T2> jaxbFactory, String rootName, String rowName)
			throws JAXBException, ParserConfigurationException {
		Node root = doc.getFirstChild();
		doc.renameNode(root, null, rootName);
		NodeList nodes = doc.getElementsByTagName("Row");
		for (int i = 0; i < nodes.getLength(); i++) {
			doc.renameNode(nodes.item(i), null, rowName);
		}
		return unmarshal(doc, type, jaxbFactory);
	}

	public static <T1, T2> T1 unmarshal(Document doc, Class<T1> type,
			Class<T2> jaxbFactory) throws JAXBException,
			ParserConfigurationException {
		JAXBContext jaxbContext = JAXBContext.newInstance(jaxbFactory);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		@SuppressWarnings("unchecked")
		JAXBElement<T1> element = (JAXBElement<T1>) jaxbUnmarshaller
				.unmarshal(doc);
		return element.getValue();
	}

	public static <T> void marshal(Object jaxbElement, Class<T> jaxbFactory,
			OutputStream out) throws JAXBException {
		if (!(jaxbElement instanceof JAXBElement<?>)) {
			throw new JAXBException("Must be a instance of JAXBElement<?>");
		}
		try {
			JAXBContext jc = JAXBContext.newInstance(jaxbFactory);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(jaxbElement, out);
		} catch (PropertyException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static Document documentify(ResultSet rs)
			throws ParserConfigurationException, SQLException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element results = doc.createElement("Results");
		doc.appendChild(results);
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCount = rsmd.getColumnCount();

		while (rs.next()) {
			Element row = doc.createElement("Row");
			results.appendChild(row);
			for (int i = 1; i <= colCount; i++) {
				String columnName = rsmd.getColumnName(i);
				Object value = rs.getObject(i);
				Element node = doc.createElement(columnName);
				node.appendChild(doc.createTextNode(value.toString()));
				row.appendChild(node);
			}
		}
		return doc;
	}

	public static Document documentify(InputStream in)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		return docBuilder.parse(in);
	}

	public static Document documentify(String xml)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		return docBuilder.parse(streamify(xml));
	}

	public static InputStream streamify(String xml) {
		return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
	}
}
