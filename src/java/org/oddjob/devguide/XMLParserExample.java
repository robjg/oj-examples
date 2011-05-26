package org.oddjob.devguide;

import java.io.File;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.xml.XMLArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class XMLParserExample {

	public static void main(String[] args) throws ArooaParseException {
	
		XMLConfiguration configuration = 
			new XMLConfiguration(new File("examples/devguide/exposed1.xml"));
		
		XMLArooaParser parser = new XMLArooaParser();
		
		parser.parse(configuration);
		
		System.out.println(parser.getXml());
	}
}
