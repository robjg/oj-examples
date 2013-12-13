package org.oddjob.userguide;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.tools.OurDirs;

public class GrabbingTest extends TestCase {

	public void testGrabbing() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/grabbing.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());		
	}
}
