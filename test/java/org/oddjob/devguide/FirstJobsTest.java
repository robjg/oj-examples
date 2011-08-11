package org.oddjob.devguide;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.Setup;
import org.oddjob.state.ParentState;

public class FirstJobsTest extends TestCase {

	File dir;

	ClassLoader classLoader;
	
	@Override
	protected void setUp() throws Exception {
		OurDirs dirs = new OurDirs();
		
		dir = new File(dirs.base(), "examples/devguide");
		
		assertTrue(dir.exists());
	
		classLoader = new Setup().getClassLoader();
	}
		
	public void testFirst1() {
		
		Oddjob oj = new Oddjob();
		oj.setClassLoader(classLoader);
		oj.setFile(new File(dir, "hello1.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
	public void testFirst2() {
		
		Oddjob oj = new Oddjob();
		oj.setClassLoader(classLoader);
		oj.setFile(new File(dir, "hello2.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
}
