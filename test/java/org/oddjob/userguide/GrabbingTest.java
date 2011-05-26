package org.oddjob.userguide;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.JobState;

import junit.framework.TestCase;

public class GrabbingTest extends TestCase {

	public void testGrabbing() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/grabbing.xml")));
		
		oddjob.load();
		
		assertEquals(JobState.READY, oddjob.lastJobStateEvent().getJobState());		
	}
}
