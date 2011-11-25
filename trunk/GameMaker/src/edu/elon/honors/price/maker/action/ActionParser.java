package edu.elon.honors.price.maker.action;

import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import android.content.Context;
import android.view.View;

import edu.elon.honors.price.data.Event.Parameters;
import edu.elon.honors.price.game.Game;

public class ActionParser implements ContentHandler {
	
	private Stack<Element> parents = new Stack<Element>();
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		parents.add(Element.genElement(qName, atts));
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (parents.size() > 1) {
			Element finished = parents.pop();
			parents.peek().addChild(finished);
		}
	}

	public Element.ParamViewHolder generateLayout(Context context) {
		if (parents.size() == 0) return null;
		return parents.peek().genView(context);
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
