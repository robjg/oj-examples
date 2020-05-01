package org.oddjob.devguide;

import java.io.File;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.standard.StandardArooaParser;
import org.oddjob.arooa.xml.XMLConfiguration;

public class StandardAPExample {

	public static void main(String... args) throws ArooaParseException {
	
		if (args.length != 1) {
			System.err.println("Usage: file-name");
			System.exit(-1);
		}
		
		XMLConfiguration configuration = 
			new XMLConfiguration(new File(args[0]));
		
		ThirdComponent component = new ThirdComponent();
		
		StandardArooaParser parser = new StandardArooaParser(component);
		
		parser.parse(configuration);
		
		ArooaSession session = parser.getSession();
		
		session.getComponentPool().configure(component);
	}
	
}
