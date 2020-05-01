package org.oddjob.blogs;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.oddjob.schedules.ScheduleContext;
import org.oddjob.schedules.ScheduleResult;
import org.oddjob.schedules.schedules.DailySchedule;

public class TimerScheduler {

	final Timer timer = new Timer();
	
	ScheduleContext context;	
	boolean stop;
	
	public TimerScheduler(Date fromDate) {
		this.context = new ScheduleContext(fromDate);
	}
	
	synchronized void schedule(final Runnable job) {

		DailySchedule schedule = new DailySchedule();
		schedule.setAt("08:00");
		final ScheduleResult next = schedule.nextDue(context);
						
		System.out.println("Scheduled at " + next.getFromDate());
		
		if (stop) {
			return;
		}
		timer.schedule(new TimerTask() {
			@Override
			public synchronized void run() {
				job.run();
				context = context.move(next.getUseNext());
				schedule(job);
			}
		}, next.getFromDate());		
	}
	
	synchronized void stop() {
		stop = true;
		timer.cancel();
	}
}
