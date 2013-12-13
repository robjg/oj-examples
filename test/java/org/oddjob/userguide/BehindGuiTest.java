package org.oddjob.userguide;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.tools.OurDirs;

public class BehindGuiTest extends TestCase {

	OurDirs dirs = new OurDirs();
		
	public void testExample1() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui1.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	public void testExample2() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui2.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	
	public void testExample3() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui3.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	
	public void testExample4() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui4.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

	
	public void testExample5() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui5.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

	
	public void testExample6() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/behindgui6.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

}
