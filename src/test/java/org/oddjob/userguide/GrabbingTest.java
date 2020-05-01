package org.oddjob.userguide;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.OurDirs;

public class GrabbingTest extends Assert {

    @Test
	public void testGrabbing() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/grabbing.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());		
	}
}
