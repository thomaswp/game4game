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
import edu.elon.honors.price.maker.action.writer.ScriptableHandler;
import edu.elon.honors.price.maker.action.writer.ScriptableWriter;
import edu.elon.honors.price.maker.action.writer.GameStateWriter;

public class Main {
	public static void main(String[] args) throws ParserConfigurationException, SAXException, FileNotFoundException, IOException {
		if (args.length == 0) {
			args = new String[] {
				new File("").getAbsolutePath() + "\\..\\GameMaker\\assets\\actions",
				new File("").getAbsolutePath() + "\\..\\GameMaker\\assets\\triggers",
				new File("").getAbsolutePath() + "\\..\\platforge-player\\core\\pregen\\com\\twp\\core\\action"
			};
		}

		File output = new File(args[2]);
		File[] files = output.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		
		parseActions(args[0], args[2]);
		parseTriggers(args[1], args[2]);
		
		StringWriter sWriter = new StringWriter();
		GameStateWriter gsWriter = new GameStateWriter(sWriter);
		gsWriter.writeHeader();
		writeFile(output.getAbsolutePath() + "\\" + "GameState.java",
				sWriter.toString());
	}
	
	private static void parseTriggers(String sourcePath, String destPath) 
			throws SAXException, FileNotFoundException, IOException {
		System.out.printf("Starting build: %s, %s\n", sourcePath, destPath);

		File[] files = new File(sourcePath).listFiles();
		XMLReader parser = XMLReaderFactory.createXMLReader();

		File output = new File(destPath);
		LinkedList<ScriptableWriter> actions = new LinkedList<ScriptableWriter>();
		if (files != null) {
			for (File file : files) {
				System.out.println("Parsing: " + file.getPath());
				ScriptableHandler handler = new ScriptableHandler(false);
				parser.setContentHandler(handler);
				parser.parse(new InputSource(new FileInputStream(file)));
				handler.writeFile(output);
				actions.add(handler.getActionWriter());
			}
		}
	}
	
	private static void parseActions(String sourcePath, String destPath) 
			throws SAXException, FileNotFoundException, IOException {

		System.out.printf("Starting build: %s, %s\n", sourcePath, destPath);
		
		File output = new File(destPath);
		File[] files = output.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
		
		files = new File(sourcePath).listFiles();
		XMLReader parser = XMLReaderFactory.createXMLReader();
		
		LinkedList<ScriptableWriter> actions = new LinkedList<ScriptableWriter>();
		if (files != null) {
			for (File file : files) {
				System.out.println("Parsing: " + file.getPath());
				ScriptableHandler handler = new ScriptableHandler(true);
				parser.setContentHandler(handler);
				parser.parse(new InputSource(new FileInputStream(file)));
				handler.writeFile(output);
				actions.add(handler.getActionWriter());
			}
		}
		
		StringWriter sWriter = new StringWriter();
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
