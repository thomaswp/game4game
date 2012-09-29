package edu.elon.honors.price.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ActionHandler implements ContentHandler {

	public void writeFile(File path) throws FileNotFoundException {
		String text = writer.toString();
		PrintWriter writer = new PrintWriter(path.getAbsolutePath() + 
				"\\" + actionWriter.fileName + ".java");
		writer.append(text);
		writer.close();
	}
	
	private StringWriter writer = new StringWriter(3000);
	private ActionWriter actionWriter;

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		if (qName.equalsIgnoreCase("action")) {
			actionWriter = new ActionWriter(qName, atts);
			actionWriter.writeHeader(writer);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		//System.out.println("End: " + qName);
	}
	
	
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startDocument() throws SAXException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
		
	}

}
