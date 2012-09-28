import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.elon.honors.price.action.ActionHandler;
import edu.elon.honors.price.data.types.Switch;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		System.out.printf("Starting build: %s, %s", args[0], args[1]);
		
		File output = new File(args[1]);
		File[] files = output.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		
		files = new File(args[0]).listFiles();
		XMLReader parser = XMLReaderFactory.createXMLReader();
		
		if (files != null) {
			for (File file : files) {
				ActionHandler handler = new ActionHandler();
				parser.setContentHandler(handler);
				parser.parse(new InputSource(new FileInputStream(file)));
				handler.writeFile(output);
			}
		}
		
		ActionSetSwitch action = null;
		if (action.setOneSwitch) {
			Switch s = action.setOneSwitchData.switch1;
		} else if (action.setAllSwitchesFrom) {
			Switch from = action.setAllSwitchesFromData.from;
			Switch to = action.setAllSwitchesFromData.to;
		}
		if (action.setItTo) {
			Switch s = action.setItToData.setToASwitchsValueData.switch1;
		}
	}
}
