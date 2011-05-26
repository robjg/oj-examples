package org.oddjob.devguide;

import java.awt.Component;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.ArooaType;
import org.oddjob.arooa.design.DesignFactory;
import org.oddjob.arooa.design.DesignInstance;
import org.oddjob.arooa.design.DesignParser;
import org.oddjob.arooa.design.GenericDesignFactory;
import org.oddjob.arooa.design.view.SwingFormFactory;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.parsing.ArooaContext;
import org.oddjob.arooa.parsing.ArooaElement;
import org.oddjob.arooa.xml.XMLConfiguration;

public class DesignParserExample {

	public static void main(String[] args) throws ArooaParseException {
	
		XMLConfiguration configuration = 
			new XMLConfiguration(new File("examples/devguide/exposed1.xml"));
		
		DesignParser parser = new DesignParser(
				new DesignFactory() {
					public DesignInstance createDesign(
							ArooaElement element, ArooaContext parentContext) {
						return new GenericDesignFactory(
								new SimpleArooaClass(ThirdComponent.class)
								).createDesign(
							element, 
							parentContext);
					}
				});
		parser.setArooaType(ArooaType.COMPONENT);
		
		parser.parse(configuration);
		
		DesignInstance design = parser.getDesign();
		Component view = SwingFormFactory.create(design.detail()).dialog();
		
		JFrame frame = new JFrame();
		frame.getContentPane().add(view);
		frame.pack();
		frame.setVisible(true);

		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
}
