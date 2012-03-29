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
	
	public TimerScheduler(Date fromDate) {
		this.context = new ScheduleContext(fromDate);
	}
	
	void schedule(final Runnable job) {

		DailySchedule schedule = new DailySchedule();
		schedule.setAt("08:00");
		final ScheduleResult next = schedule.nextDue(context);
						
		System.out.println("Scheduled at " + next.getFromDate());
		
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				job.run();
				context = context.move(next.getUseNext());
				schedule(job);
			}
		}, next.getFromDate());		
	}
}
