package org.oddjob.devguide;

import java.io.File;

import org.apache.log4j.Logger;
import org.oddjob.Oddjob;
import org.oddjob.OddjobSessionFactory;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.LifecycleInterfaceFactory;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;

import junit.framework.TestCase;

public class AnnotationsTest extends TestCase {

	
	private static final Logger logger = Logger.getLogger(AnnotationsTest.class);

	
	public void testArooaLifecycle() {
		
		ArooaSession session = new OddjobSessionFactory().createSession();
		
		Object component;
		ArooaLifeAware lifecycle; 
		
		component = new DestroyByAnnotation();
		
		lifecycle = 
				new LifecycleInterfaceFactory().lifeCycleFor(component, session);
		
		assertNotNull(lifecycle);
		
		lifecycle.destroy();
		
		component = new DestroyByMagic();
		
		lifecycle = 
				new LifecycleInterfaceFactory().lifeCycleFor(component, session);
		
		assertNotNull(lifecycle);
		
		lifecycle.destroy();
	}
	
	public void testDestroyExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/annotations.xml");
		
		ConsoleCapture console = new ConsoleCapture();
		console.capture(Oddjob.CONSOLE);
			
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(config);
		
		oddjob.run();

		
		
		assertEquals(0, console.size());
		
		oddjob.destroy();
		
		console.close();
		
		console.dump(logger);
		
		String[] lines = console.getLines();
				
		assertEquals("You never loved me!", lines[0].trim());
		assertEquals("How can you do this to me?!", lines[1].trim());
		
		assertEquals(2, lines.length);
	}
}
