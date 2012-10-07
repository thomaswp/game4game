import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import edu.elon.honors.price.maker.action.writer.ActionFactoryWriter;
import edu.elon.honors.price.maker.action.writer.ActionHandler;
import edu.elon.honors.price.maker.action.writer.ActionWriter;
import edu.elon.honors.price.maker.action.writer.GameStateWriter;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		if (args.length == 0) {
			args = new String[] {
				new File("").getAbsolutePath() + "\\assets",
				new File("").getAbsolutePath() + "\\pregen\\edu\\elon\\honors\\price\\maker\\action"
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
		
		LinkedList<ActionWriter> actions = new LinkedList<ActionWriter>();
		if (files != null) {
			for (File file : files) {
				System.out.println("Parsing: " + file.getPath());
				ActionHandler handler = new ActionHandler();
				parser.setContentHandler(handler);
				parser.parse(new InputSource(new FileInputStream(file)));
				handler.writeFile(output);
				actions.add(handler.getActionWriter());
			}
		}
		
		StringWriter sWriter = new StringWriter();
		GameStateWriter gsWriter = new GameStateWriter(sWriter);
		gsWriter.writeHeader();
		writeFile(output.getAbsolutePath() + "\\" + "GameState.java",
				sWriter.toString());
		
		sWriter = new StringWriter();
		ActionFactoryWriter afWriter = 
				new ActionFactoryWriter(sWriter, actions);
		afWriter.writeHeader();
		writeFile(output.getAbsolutePath() + "\\" + "ActionFactory.java",
				sWriter.toString());
	}
	
	private static void writeFile(String path, String text) throws IOException {
		FileWriter fWriter = new FileWriter(path);
		fWriter.write(text);
		fWriter.close();
	}
}
