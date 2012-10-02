import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.elon.honors.price.data.types.Switch;
import edu.elon.honors.price.maker.action.ActionHandler;
import edu.elon.honors.price.maker.action.GameStateWriter;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		if (args.length == 0) {
			args = new String[] {
				new File("").getAbsolutePath() + "\\assets",
				new File("").getAbsolutePath() + "\\gen\\edu\\elon\\honors\\price\\maker\\action"
			};
		}
		
		System.out.printf("Starting build: %s, %s\n", args[0], args[1]);
		
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
				System.out.println("Parsing: " + file.getPath());
				ActionHandler handler = new ActionHandler();
				parser.setContentHandler(handler);
				parser.parse(new InputSource(new FileInputStream(file)));
				handler.writeFile(output);
			}
		}
		
		StringWriter writer = new StringWriter();
		GameStateWriter gsWriter = new GameStateWriter(writer);
		gsWriter.writeHeader();
		FileWriter fWriter = new FileWriter(output.getAbsolutePath() + "\\" + "GameState.java");
		fWriter.write(writer.toString());
		fWriter.close();
	}
}
