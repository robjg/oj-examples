package org.oddjob.devguide;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.oddjob.ConsoleCapture;
import org.oddjob.FailedToStopException;
import org.oddjob.Helper;
import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.StateSteps;
import org.oddjob.Stateful;
import org.oddjob.Stoppable;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.JobState;

public class MoreJobsTest extends TestCase {
	private static final Logger logger = Logger.getLogger(
			MoreJobsTest.class);
	
	public void testNaughtyJob() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/naughty.xml")));
		
		oddjob.run();
		
		assertEquals(JobState.EXCEPTION, 
				oddjob.lastJobStateEvent().getJobState());
		
		Stateful stateful = (Stateful) Helper.getChildren(oddjob)[0];

		Throwable e = stateful.lastJobStateEvent().getException();
		
		assertEquals("I won't run. I won't!", e.getMessage());
		
		oddjob.destroy();
	}
	
	public void testNotCompleteJob() {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/notcomplete.xml")));
				
		oddjob.run();
		
		assertEquals(JobState.INCOMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
	public void testStopingJob() throws InterruptedException, FailedToStopException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/stopping.xml")));
		
		oddjob.load();
		
		Stateful stopping = (Stateful) Helper.getChildren(oddjob)[0];

		
		StateSteps states = new StateSteps(stopping);
				
		states.startCheck(JobState.READY, JobState.EXECUTING);
		
		Thread thread = new Thread(oddjob);
		thread.start();
		
		states.checkWait();
		
		Thread.sleep(1000);
		
		((Stoppable) stopping).stop();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
	public void testService() throws InterruptedException, FailedToStopException {
		
		OurDirs dirs = new OurDirs();
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/devguide/service.xml")));
		
		ConsoleCapture console = new ConsoleCapture();
		console.capture(Oddjob.CONSOLE);
				
		oddjob.load();
		
		Stateful service = (Stateful) Helper.getChildren(oddjob)[0];

		
		StateSteps states = new StateSteps(service);
				
		states.startCheck(JobState.READY, JobState.EXECUTING);
		
		oddjob.run();
		
		states.checkWait();
		
		Thread.sleep(1000);
		
		((Stoppable) service).stop();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		console.close();
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals("I could be useful.", lines[0].trim());
		assertEquals("OK - I'll stop.", lines[1].trim());
		
		assertEquals(2, lines.length);
		
		oddjob.destroy();
	}
}
