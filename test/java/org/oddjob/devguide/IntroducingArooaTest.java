package org.oddjob.devguide;
import org.junit.Before;

import org.junit.Test;

import java.io.File;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;
import org.oddjob.tools.OurDirs;

public class IntroducingArooaTest extends Assert {

	File dir;
	
    @Before
    public void setUp() throws Exception {
		
		dir = new File(
				new OurDirs().base(), "examples/devguide");
		
		assertTrue(dir.exists());
	}
	
    @Test
	public void testArooa1() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
    @Test
	public void testArooa2() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component2.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
    @Test
	public void testArooa3() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component3.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());
		
		oj.destroy();
	}
	
    @Test
	public void testArooa4() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "properties.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}
	
    @Test
	public void testArooa5() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "properties2.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}

    @Test
	public void testHelloPerson() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "helloperson.xml"));
		oj.run();

		assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

		oj.destroy();
	}
	
}
