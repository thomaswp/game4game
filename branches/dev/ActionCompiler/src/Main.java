import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import compiler.ActionHandler;




public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		File output = new File(args[1]);
		File[] files = output.listFiles();
		for (File file : files) {
			file.delete();
		}
		
		files = new File(args[0]).listFiles();
		XMLReader parser = XMLReaderFactory.createXMLReader();
		
		for (File file : files) {
			ActionHandler handler = new ActionHandler();
			parser.setContentHandler(handler);
			parser.parse(new InputSource(new FileInputStream(file)));
			handler.writeFile(output);
		}
	}
}
