package org.oddjob.devguide;
import org.junit.Before;

import org.junit.Test;

import java.io.File;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.Setup;
import org.oddjob.state.ParentState;
import org.oddjob.OurDirs;

public class FirstJobsTest extends Assert {

	File dir;

	ClassLoader classLoader;
	
    @Before
    public void setUp() throws Exception {
		OurDirs dirs = new OurDirs();
		
		dir = new File(dirs.base(), "examples/devguide");
		
		assertTrue(dir.exists());
	
		classLoader = new Setup().getClassLoader();
	}
		
    @Test
	public void testFirst1() {
		
		Oddjob oj = new Oddjob();
		oj.setClassLoader(classLoader);
		oj.setFile(new File(dir, "hello1.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
    @Test
	public void testFirst2() {
		
		Oddjob oj = new Oddjob();
		oj.setClassLoader(classLoader);
		oj.setFile(new File(dir, "hello2.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
}
