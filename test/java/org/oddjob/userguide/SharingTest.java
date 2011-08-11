package org.oddjob.userguide;

import junit.framework.TestCase;

import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;

public class SharingTest extends TestCase {

	public void testMain() throws ArooaPropertyException, ArooaConversionException, FailedToStopException {
				
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/sharingMain.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		/* Can't do this yet because we can't kill jobs.
		
		assertEquals(JobState.EXECUTING, oddjob.lastJobStateEvent().getJobState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String text1 = lookup.lookup("server1/server-jobs1/echo1.text", 
				String.class);
		
		assertEquals("Important Job 1", text1);
		
		oddjob.stop();
		
		assertEquals(JobState.COMPLETE, oddjob.lastJobStateEvent().getJobState());
		
		*/
		
		oddjob.destroy();
	}
}
