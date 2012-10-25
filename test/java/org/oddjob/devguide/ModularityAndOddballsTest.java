package org.oddjob.devguide;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.state.ParentState;

public class ModularityAndOddballsTest extends TestCase {

	File dir;
	
	File base;
	
	@Override
	protected void setUp() throws Exception {
		
		base = new OurDirs().base();
		dir = new File(base, "examples/devguide");
		
		assertTrue(dir.exists());
	}
	
	public void testHelloPersonMain() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "helloperson-main.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}
	
	public void testHelloPersonMain2() {
		
		Properties props = new Properties();		
		props.setProperty("my-app.dir", 
				new File(base, "../oj-ant").getPath());

		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "myapp-main.xml"));
		oj.setProperties(props);
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}

	public void testExtendingOddballClassLoader() {
		
		Properties props = new Properties();		
		props.setProperty("my-app.dir", 
				new File(base, "../oj-ant").getPath());

		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "myapp2-main.xml"));
		oj.setProperties(props);
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}
}
