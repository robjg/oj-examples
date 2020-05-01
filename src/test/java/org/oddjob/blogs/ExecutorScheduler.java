package org.oddjob.blogs;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.oddjob.schedules.ScheduleContext;
import org.oddjob.schedules.ScheduleResult;
import org.oddjob.schedules.schedules.DailySchedule;

public class ExecutorScheduler {

	final ScheduledExecutorService executor =
			Executors.newScheduledThreadPool(3);
	
	ScheduleContext context;	
	boolean stop;
	
	public ExecutorScheduler(Date date) {
		this.context = new ScheduleContext(date);
	}
	
	synchronized void schedule(final Runnable job) {
				
		DailySchedule schedule = new DailySchedule();
		schedule.setAt("08:00");
		final ScheduleResult next = schedule.nextDue(context);
		
		System.out.println("Scheduled at " + next.getFromDate());
		
		long delay = next.getFromDate().getTime() - 
				System.currentTimeMillis();  
		
		if (stop) {
			return;
		}
		
		executor.schedule(new Runnable() {
			@Override
			public synchronized void run() {
				job.run();
				context = context.move(next.getUseNext());
				schedule(job);
			}
		}, delay, TimeUnit.MILLISECONDS);		
	}
	
	synchronized void stop() {
		stop = true;
		executor.shutdownNow();
	}
}
