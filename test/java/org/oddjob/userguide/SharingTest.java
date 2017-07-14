package org.oddjob.userguide;

import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.tools.OurDirs;

import junit.framework.TestCase;

public class SharingTest extends TestCase {

	public void testMain() throws ArooaPropertyException, ArooaConversionException, FailedToStopException {
				
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/sharingMain.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.run();
		
		/* Can't do this yet because we can't kill exec jobs on windows.
		
		assertEquals(ParentState.EXECUTING, oddjob.lastStateEvent().getState());
		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		String text1 = lookup.lookup("server1/server-jobs1/echo1.text", 
				String.class);
		
		assertEquals("Important Job 1", text1);
		
		oddjob.stop();
		
		assertEquals(JobState.COMPLETE, oddjob.lastStateEvent().getState());
		
		*/
		
		oddjob.destroy();
	}
}
