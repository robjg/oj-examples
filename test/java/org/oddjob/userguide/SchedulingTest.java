package org.oddjob.userguide;

import java.text.ParseException;

import junit.framework.TestCase;

import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.scheduling.ManualClock;
import org.oddjob.scheduling.Timer;
import org.oddjob.state.JobState;
import org.oddjob.util.Clock;

public class SchedulingTest extends TestCase {

	OurDirs dirs = new OurDirs();
		
	public void testExample1() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling1.xml")));
		
		oddjob.load();
		
		assertEquals(JobState.READY, oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
	public void testExample2() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling2.xml")));
		
		oddjob.load();
		
		assertEquals(JobState.READY, oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
	
	public void testExample3() throws ParseException, FailedToStopException, ArooaPropertyException, ArooaConversionException {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling3.xml")));
		
		oddjob.load();
		
		assertEquals(JobState.READY, oddjob.lastJobStateEvent().getJobState());
		
		Timer timer = new OddjobLookup(oddjob).lookup("timer", Timer.class);
		
		Clock clock = new ManualClock("2010-05-03 06:00");

		timer.setClock(clock);
		
		oddjob.run();
		
		assertEquals(JobState.EXECUTING, 
				oddjob.lastJobStateEvent().getJobState());
		
		assertEquals(
				DateHelper.parseDateTime("2010-05-04 07:00"),
				timer.getNextDue());
		
		oddjob.stop();
		
		assertEquals(JobState.COMPLETE, 
				oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
	
	public void testExample4() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling4.xml")));
		
		oddjob.load();
		
		assertEquals(JobState.READY, oddjob.lastJobStateEvent().getJobState());
		
		oddjob.destroy();
	}
	
}
