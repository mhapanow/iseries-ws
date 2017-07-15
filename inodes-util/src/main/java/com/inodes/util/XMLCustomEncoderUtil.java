package com.inodes.util;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLCustomEncoderUtil {

	/**
	 * This attribute creates all the elements
	 */
	private Document document;
	
	/**
	 * This element represents the root of the XML
	 */
	private Element root;

	/**
	 * Parameterized constructor
	 * @throws ParserConfigurationException
	 */
	public XMLCustomEncoderUtil() throws ParserConfigurationException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		document = docBuilder.newDocument();
	}

	/**
	 * In this method, the root is defined
	 * @param name
	 */
	public void addRoot(String name) {
		root = document.createElement(name);
		document.appendChild(root);
	}

	/**
	 * This method adds an attribute for an specific element
	 * @param elementId
	 * @param name
	 * @param value
	 */
	public void addAtributte(String elementId, String name, String value) {
		Attr attr = document.createAttribute(name);
		attr.setValue(value);
		root.setAttributeNode(attr);
	}
	
	/**
	 * This method adds an element as root child
	 * @param name
	 * @param value
	 */
	public void addElement(String name, String value) {
		Element element = document.createElement(name);
		element.appendChild(document.createTextNode(value));
		root.appendChild(element);
	}
	
	/**
	 * This method adds an element as child of an specific father
	 * @param name
	 * @param value
	 */
	public void addElement(String fatherId, String name, String value) {
		Element father = document.getElementById(fatherId);
		
		if(father == null)
			return;
		
		Element element = document.createElement(name);
		element.appendChild(document.createTextNode(value));
		father.appendChild(element);
	}

	/**
	 * This method generates XML source with the content
	 * @return
	 */
	public String toXML() {
		try {
			StringWriter sw = new StringWriter();
			DOMSource source = new DOMSource(document);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
	        transformer.setOutputProperty(OutputKeys.INDENT, "no");
	        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
	        transformer.transform(source, new StreamResult(sw));

			return sw.toString();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This method generates Document DOM with the XML string
	 * @param String xmlString
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public static Document fromXML(String xmlString) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(xmlString)));
	}
}