package org.oddjob.blogs;

import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ScheduledExecutorService;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.schedules.Schedule;
import org.oddjob.schedules.ScheduleContext;
import org.oddjob.schedules.ScheduleList;
import org.oddjob.schedules.ScheduleResult;
import org.oddjob.schedules.schedules.BrokenSchedule;
import org.oddjob.schedules.schedules.DailySchedule;
import org.oddjob.schedules.schedules.DateSchedule;
import org.oddjob.schedules.schedules.WeeklySchedule;
import org.oddjob.schedules.units.DayOfWeek;
import org.oddjob.tools.ConsoleCapture;

public class AnotherTakeOnSchedulingTest extends TestCase {

	public void testSimpleDailyExample() throws ParseException {

		final long time = DateHelper.parseDateTime("2012-03-28 20:15")
				.getTime();

		class Date extends java.util.Date {
			private static final long serialVersionUID = 2012041200L;

			public Date() {
				super(time);
			}
		}

		java.util.Date[] expected = new java.util.Date[] {
				DateHelper.parseDateTime("2012-03-29 08:00"),
				DateHelper.parseDateTime("2012-03-30 08:00"),
				DateHelper.parseDateTime("2012-03-31 08:00"),
				DateHelper.parseDateTime("2012-04-01 08:00"),
				DateHelper.parseDateTime("2012-04-02 08:00"),
				DateHelper.parseDateTime("2012-04-03 08:00"),
				DateHelper.parseDateTime("2012-04-04 08:00"), };

		DailySchedule mySchedule = new DailySchedule();
		mySchedule.setAt("08:00");

		ScheduleContext context = new ScheduleContext(new Date());

		for (int i = 0; i < 7; ++i) {
			ScheduleResult result = mySchedule.nextDue(context);
			System.out.println("Next due: " + result.getFromDate());
			context = context.move(result.getUseNext());
			assertEquals(expected[i], result.getFromDate());
		}

	}

	public void testUnusedExample() throws ParseException {

		final long time = DateHelper.parseDateTime("2012-03-28 20:15")
				.getTime();

		class Date extends java.util.Date {
			private static final long serialVersionUID = 2012041200L;

			public Date() {
				super(time);
			}
		}

		java.util.Date[] expected = new java.util.Date[] {
				DateHelper.parseDateTime("2012-03-29 09:00"),
				DateHelper.parseDateTime("2012-03-30 09:00"),
				DateHelper.parseDateTime("2012-04-02 10:00"),
				DateHelper.parseDateTime("2012-04-03 10:00"),
				DateHelper.parseDateTime("2012-04-04 10:00"),
				DateHelper.parseDateTime("2012-04-05 09:00"), };

		DailySchedule tenAMSchedule = new DailySchedule();
		tenAMSchedule.setAt("10:00");

		WeeklySchedule monToWedSchedule = new WeeklySchedule();
		monToWedSchedule.setFrom(DayOfWeek.Days.MONDAY);
		monToWedSchedule.setTo(DayOfWeek.Days.WEDNESDAY);
		monToWedSchedule.setRefinement(tenAMSchedule);

		DailySchedule nineAMSchedule = new DailySchedule();
		nineAMSchedule.setAt("9:00");

		WeeklySchedule thuFriSchedule = new WeeklySchedule();
		thuFriSchedule.setFrom(DayOfWeek.Days.THURSDAY);
		thuFriSchedule.setTo(DayOfWeek.Days.FRIDAY);
		thuFriSchedule.setRefinement(nineAMSchedule);

		ScheduleList mySchedule = new ScheduleList();
		mySchedule.setSchedules(new Schedule[] { monToWedSchedule,
				thuFriSchedule });

		ScheduleContext context = new ScheduleContext(new Date());

		for (int i = 0; i < 6; ++i) {
			ScheduleResult result = mySchedule.nextDue(context);
			System.out.println("Next due: " + result.getFromDate());
			context = context.move(result.getUseNext());
			assertEquals(expected[i], result.getFromDate());
		}

	}

	public void testBreakExample() throws ParseException {

		final long time = DateHelper.parseDateTime("2012-04-03 20:15")
				.getTime();

		class Date extends java.util.Date {
			private static final long serialVersionUID = 2012041200L;

			public Date() {
				super(time);
			}
		}

		DailySchedule dailySchedule = new DailySchedule();
		dailySchedule.setAt("08:00");

		WeeklySchedule weeklySchedule = new WeeklySchedule();
		weeklySchedule.setFrom(DayOfWeek.Days.MONDAY);
		weeklySchedule.setTo(DayOfWeek.Days.FRIDAY);
		weeklySchedule.setRefinement(dailySchedule);

		{
			System.out.println("Weekly Schedule:");
			java.util.Date[] expected = new java.util.Date[] {
					DateHelper.parseDateTime("2012-04-04 08:00"),
					DateHelper.parseDateTime("2012-04-05 08:00"),
					DateHelper.parseDateTime("2012-04-06 08:00"),
					DateHelper.parseDateTime("2012-04-09 08:00"),
					DateHelper.parseDateTime("2012-04-10 08:00"), };

			ScheduleContext context = new ScheduleContext(new Date());

			for (int i = 0; i < expected.length; ++i) {
				ScheduleResult result = weeklySchedule.nextDue(context);
				System.out.println("Next due: " + result.getFromDate());
				context = context.move(result.getUseNext());
				assertEquals(expected[i], result.getFromDate());
			}
		}

		DateSchedule goodFriday = new DateSchedule();
		goodFriday.setOn("2012-04-06");

		DateSchedule easterMonday = new DateSchedule();
		easterMonday.setOn("2012-04-09");

		ScheduleList holidayList = new ScheduleList();
		holidayList.setSchedules(new Schedule[] { goodFriday, easterMonday });

		{
			System.out.println("Holiday Schedule:");

			java.util.Date[] expected = new java.util.Date[] {
					DateHelper.parseDateTime("2012-04-06 00:00"),
					DateHelper.parseDateTime("2012-04-09 00:00"), };

			ScheduleContext context = new ScheduleContext(new Date());

			for (int i = 0; i < expected.length; ++i) {
				ScheduleResult result = holidayList.nextDue(context);
				System.out.println("Next due: " + result.getFromDate());
				context = context.move(result.getUseNext());
				assertEquals(expected[i], result.getFromDate());
			}
		}

		BrokenSchedule brokenSchedule = new BrokenSchedule();
		brokenSchedule.setSchedule(weeklySchedule);
		brokenSchedule.setBreaks(holidayList);

		{
			System.out.println("Broken Schedule:");

			java.util.Date[] expected = new java.util.Date[] {
					DateHelper.parseDateTime("2012-04-04 08:00"),
					DateHelper.parseDateTime("2012-04-05 08:00"),
					DateHelper.parseDateTime("2012-04-10 08:00"),
					DateHelper.parseDateTime("2012-04-11 08:00"), };

			ScheduleContext context = new ScheduleContext(new Date());

			for (int i = 0; i < expected.length; ++i) {
				ScheduleResult result = brokenSchedule.nextDue(context);
				System.out.println("Next due: " + result.getFromDate());
				context = context.move(result.getUseNext());
				assertEquals(expected[i], result.getFromDate());
			}
		}

		DailySchedule holidayTimes = new DailySchedule();
		holidayTimes.setAt("11:00");

		brokenSchedule.setAlternative(holidayTimes);

		{
			System.out.println("Alternate Schedule:");

			java.util.Date[] expected = new java.util.Date[] {
					DateHelper.parseDateTime("2012-04-04 08:00"),
					DateHelper.parseDateTime("2012-04-05 08:00"),
					DateHelper.parseDateTime("2012-04-06 11:00"),
					DateHelper.parseDateTime("2012-04-09 11:00"),
					DateHelper.parseDateTime("2012-04-10 08:00"),
					DateHelper.parseDateTime("2012-04-11 08:00"), };

			ScheduleContext context = new ScheduleContext(new Date());

			for (int i = 0; i < expected.length; ++i) {
				ScheduleResult result = brokenSchedule.nextDue(context);
				System.out.println("Next due: " + result.getFromDate());
				context = context.move(result.getUseNext());
				assertEquals(expected[i], result.getFromDate());
			}
		}
	}

	public void testTimerExample() throws ParseException, InterruptedException {

		final long time = DateHelper.parseDateTime("2012-03-29 07:59:59:999")
				.getTime();

		class Date extends java.util.Date {
			private static final long serialVersionUID = 2012041200L;

			public Date() {
				super(time);
			}
		}

		ConsoleCapture capture = new ConsoleCapture();
		capture.capture(Oddjob.CONSOLE);

		TimerScheduler scheduler = new TimerScheduler(new Date());
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello");
			}
		});
		Timer timer = scheduler.timer;

		String[] lines = null;
		for (int i = 0; i < 10; ++i) {
			lines = capture.getLines();
			if (lines.length > 2) {
				break;
			}
			Thread.sleep(500);
		}

		capture.close();

		assertTrue(lines != null && lines.length > 2);

		assertEquals(
				"Scheduled at "
						+ DateHelper.parseDateTime("2012-03-29 08:00")
								.toString(), lines[0].trim());
		assertEquals("Hello", lines[1].trim());
		assertEquals(
				"Scheduled at "
						+ DateHelper.parseDateTime("2012-03-30 08:00")
								.toString(), lines[2].trim());

		timer.cancel();
	}

	public void testExecutorScheduler() throws ParseException,
			InterruptedException {

		final long time = DateHelper.parseDateTime("2012-03-29 07:59:59:999")
				.getTime();

		class Date extends java.util.Date {
			private static final long serialVersionUID = 2012041200L;

			public Date() {
				super(time);
			}
		}

		ConsoleCapture capture = new ConsoleCapture();
		capture.capture(Oddjob.CONSOLE);

		ExecutorScheduler scheduler = new ExecutorScheduler(new Date());
		scheduler.schedule(new Runnable() {
			@Override
			public void run() {
				System.out.println("Hello");
			}
		});
		ScheduledExecutorService executor = scheduler.executor;

		String[] lines = null;
		for (int i = 0; i < 10; ++i) {
			lines = capture.getLines();
			if (lines.length > 2) {
				break;
			}
			Thread.sleep(500);
		}

		capture.close();

		assertTrue(lines != null && lines.length > 2);

		assertEquals(
				"Scheduled at "
						+ DateHelper.parseDateTime("2012-03-29 08:00")
								.toString(), lines[0].trim());
		assertEquals("Hello", lines[1].trim());
		assertEquals(
				"Scheduled at "
						+ DateHelper.parseDateTime("2012-03-30 08:00")
								.toString(), lines[2].trim());

		executor.shutdown();
	}
}
