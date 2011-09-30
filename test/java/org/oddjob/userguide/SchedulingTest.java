package org.oddjob.userguide;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

import org.oddjob.FailedToStopException;
import org.oddjob.MockOddjobExecutors;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.scheduling.ManualClock;
import org.oddjob.scheduling.MockScheduledExecutorService;
import org.oddjob.scheduling.MockScheduledFuture;
import org.oddjob.scheduling.Timer;
import org.oddjob.state.ParentState;

public class SchedulingTest extends TestCase {

	OurDirs dirs = new OurDirs();
		
	public void testExample1() throws ArooaPropertyException, ArooaConversionException, FailedToStopException {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling1.xml")));
		
		Date now = new Date();		
		
		oddjob.run();
		
		assertEquals(ParentState.ACTIVE, oddjob.lastStateEvent().getState());
		
		while (true) {
			
			OddjobLookup lookup = new OddjobLookup(oddjob);

			Date nextDue = lookup.lookup("timer1.nextDue", Date.class);
			
			if (nextDue.before(now)) {
				continue;
			}
			
			nextDue = lookup.lookup("timer2.nextDue", Date.class);
			
			if (nextDue.before(now)) {
				continue;
			}
			
			break;
		}
		
		oddjob.stop();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	public void testExample2() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling2.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}

	private class OurExecutor extends MockScheduledExecutorService {
		
		Runnable task;
		
		@Override
		public ScheduledFuture<?> schedule(Runnable command, long delay,
				TimeUnit unit) {
			this.task = command;
			return new MockScheduledFuture<Void>() {
				@Override
				public boolean cancel(boolean mayInterruptIfRunning) {
					return true;
				}
			};
		}
		
		void run() {
			task.run();
			task = null;
		}
	}
	
	
	public void testExample3() throws ParseException, FailedToStopException, ArooaPropertyException, ArooaConversionException {
		
		final OurExecutor executor = new OurExecutor();
		
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling3.xml")));
		oddjob.setOddjobExecutors(new MockOddjobExecutors() {
			
			@Override
			public ScheduledExecutorService getScheduledExecutor() {
				return executor;
			}
		});
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		Timer timer = new OddjobLookup(oddjob).lookup("timer", Timer.class);
		
		ManualClock clock = new ManualClock("2010-05-03 06:00");

		timer.setClock(clock);
		
		oddjob.run();
		
		assertEquals(ParentState.ACTIVE, 
				oddjob.lastStateEvent().getState());
		
		assertEquals(
				DateHelper.parseDateTime("2010-05-04 07:00"),
				timer.getNextDue());
		
		clock.setDate("2010-05-04 07:00");
		
		executor.run();
		
		assertEquals(
				DateHelper.parseDateTime("2010-05-05 07:00"),
				timer.getNextDue());
		
		oddjob.stop();
		
		assertEquals(ParentState.READY, 
				oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
	
	
	public void testExample4() {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration(
				dirs.relative("examples/userguide/scheduling4.xml")));
		
		oddjob.load();
		
		assertEquals(ParentState.READY, oddjob.lastStateEvent().getState());
		
		oddjob.destroy();
	}
}
