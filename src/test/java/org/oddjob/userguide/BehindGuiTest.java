package org.oddjob.userguide;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.OurDirs;

public class BehindGuiTest extends Assert {

	OurDirs dirs = new OurDirs();
		
    @Test
	public void testExample1() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui1.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
    @Test
	public void testExample2() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui2.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	
    @Test
	public void testExample3() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui3.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	
    @Test
	public void testExample4() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui4.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

	
    @Test
	public void testExample5() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui5.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

	
    @Test
	public void testExample6() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui6.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

}
