package org.oddjob.devguide;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.Oddjob;
import org.oddjob.OddjobSessionFactory;
import org.oddjob.arooa.ArooaSession;
import org.oddjob.arooa.life.ArooaLifeAware;
import org.oddjob.arooa.life.LifecycleInterfaceFactory;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;

import org.junit.Assert;

public class AnnotationsTest extends Assert {

	
	private static final Logger logger = LoggerFactory.getLogger(AnnotationsTest.class);

	@Rule public TestName name = new TestName();

    @Before
    public void setUp() throws Exception {

		
		logger.info("---------------------  " + name.getMethodName()  + "  -------------------------");
	}
	
    @Test
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
	
    @Test
	public void testDestroyExample() {
		
		OurDirs dirs = new OurDirs();
		
		File config = dirs.relative("examples/devguide/annotations.xml");
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			Oddjob oddjob = new Oddjob();
			oddjob.setFile(config);
			
			oddjob.run();
	
			assertEquals(0, console.size());
			
			oddjob.destroy();
		}	
			
		console.dump(logger);
		
		String[] lines = console.getLines();
				
		assertEquals("You never loved me!", lines[0].trim());
		assertEquals("How can you do this to me?!", lines[1].trim());
		
		assertEquals(2, lines.length);
	}
}
