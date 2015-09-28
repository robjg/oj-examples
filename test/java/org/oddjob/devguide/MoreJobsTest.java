package org.oddjob.devguide;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.Stateful;
import org.oddjob.Stoppable;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OddjobTestHelper;
import org.oddjob.tools.OurDirs;
import org.oddjob.tools.StateSteps;

public class MoreJobsTest extends TestCase {
	private static final Logger logger = Logger.getLogger(
			MoreJobsTest.class);
	
	public void testNaughtyJob() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/naughty.xml")));
		
		oddjob.run();
		
		assertEquals(ParentState.EXCEPTION, 
				oddjob.lastStateEvent().getState());
		
		Stateful stateful = (Stateful) OddjobTestHelper.getChildren(oddjob)[0];

		Throwable e = stateful.lastStateEvent().getException();
		
		assertEquals("I won't run. I won't!", e.getMessage());
		
		oddjob.destroy();
	}
	
	public void testNotCompleteJob() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/notcomplete.xml")));
				
		oddjob.run();
		
		assertEquals(ParentState.INCOMPLETE, 
				oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	public void testStopingJob() throws InterruptedException, FailedToStopException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/stopping.xml")));
		
		oddjob.load();
		
		Stateful stopping = (Stateful) OddjobTestHelper.getChildren(oddjob)[0];

		
		StateSteps states = new StateSteps(stopping);
				
		states.startCheck(JobState.READY, JobState.EXECUTING);
		
		Thread thread = new Thread(oddjob);
		thread.start();
		
		states.checkWait();
		
		Thread.sleep(1000);
		
		((Stoppable) stopping).stop();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	public void testService() throws InterruptedException, FailedToStopException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/service.xml")));
		
		ConsoleCapture console = new ConsoleCapture();
		console.captureConsole();
				
		oddjob.run();
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		console.close();
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals("I could be useful.", lines[0].trim());
		assertEquals("Service Has Started.", lines[1].trim());
		assertEquals("Service Stopping.", lines[2].trim());
		assertEquals("Service Has Stopped.", lines[3].trim());
		
		assertEquals(4, lines.length);
		
		oddjob.destroy();
	}
}
