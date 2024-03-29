package org.oddjob.blogs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnotherTakeOnSchedulingTest extends Assert {

    private static final Logger logger = LoggerFactory.getLogger(
            AnotherTakeOnSchedulingTest.class);

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() throws Exception {
        logger.info("--------------------------  " + name.getMethodName() +
                "  --------------------------------");
    }

    @Test
    public void testSimpleDailyExample() {

        final long time = DateHelper.parseDateTime("2012-03-28 20:15")
                .getTime();

        class Date extends java.util.Date {
            private static final long serialVersionUID = 2012041200L;

            public Date() {
                super(time);
            }
        }

        java.util.Date[] expected = new java.util.Date[]{
                DateHelper.parseDateTime("2012-03-29 08:00"),
                DateHelper.parseDateTime("2012-03-30 08:00"),
                DateHelper.parseDateTime("2012-03-31 08:00"),
                DateHelper.parseDateTime("2012-04-01 08:00"),
                DateHelper.parseDateTime("2012-04-02 08:00"),
                DateHelper.parseDateTime("2012-04-03 08:00"),
                DateHelper.parseDateTime("2012-04-04 08:00"),};

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

    @Test
    public void testUnusedExample() {

        final long time = DateHelper.parseDateTime("2012-03-28 20:15")
                .getTime();

        class Date extends java.util.Date {
            private static final long serialVersionUID = 2012041200L;

            public Date() {
                super(time);
            }
        }

        java.util.Date[] expected = new java.util.Date[]{
                DateHelper.parseDateTime("2012-03-29 09:00"),
                DateHelper.parseDateTime("2012-03-30 09:00"),
                DateHelper.parseDateTime("2012-04-02 10:00"),
                DateHelper.parseDateTime("2012-04-03 10:00"),
                DateHelper.parseDateTime("2012-04-04 10:00"),
                DateHelper.parseDateTime("2012-04-05 09:00"),};

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
        mySchedule.setSchedules(new Schedule[]{monToWedSchedule,
                thuFriSchedule});

        ScheduleContext context = new ScheduleContext(new Date());

        for (int i = 0; i < 6; ++i) {
            ScheduleResult result = mySchedule.nextDue(context);
            assert result != null;
            System.out.println("Next due: " + result.getFromDate());
            context = context.move(result.getUseNext());
            assertEquals(expected[i], result.getFromDate());
        }

    }

    @Test
    public void testBreakExample() {

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
            java.util.Date[] expected = new java.util.Date[]{
                    DateHelper.parseDateTime("2012-04-04 08:00"),
                    DateHelper.parseDateTime("2012-04-05 08:00"),
                    DateHelper.parseDateTime("2012-04-06 08:00"),
                    DateHelper.parseDateTime("2012-04-09 08:00"),
                    DateHelper.parseDateTime("2012-04-10 08:00"),};

            ScheduleContext context = new ScheduleContext(new Date());

            for (java.util.Date date : expected) {
                ScheduleResult result = weeklySchedule.nextDue(context);
                System.out.println("Next due: " + result.getFromDate());
                context = context.move(result.getUseNext());
                assertEquals(date, result.getFromDate());
            }
        }

        DateSchedule goodFriday = new DateSchedule();
        goodFriday.setOn("2012-04-06");

        DateSchedule easterMonday = new DateSchedule();
        easterMonday.setOn("2012-04-09");

        ScheduleList holidayList = new ScheduleList();
        holidayList.setSchedules(new Schedule[]{goodFriday, easterMonday});

        {
            System.out.println("Holiday Schedule:");

            java.util.Date[] expected = new java.util.Date[]{
                    DateHelper.parseDateTime("2012-04-06 00:00"),
                    DateHelper.parseDateTime("2012-04-09 00:00"),};

            ScheduleContext context = new ScheduleContext(new Date());

            for (java.util.Date date : expected) {
                ScheduleResult result = holidayList.nextDue(context);
                assert result != null;
                System.out.println("Next due: " + result.getFromDate());
                context = context.move(result.getUseNext());
                assertEquals(date, result.getFromDate());
            }
        }

        BrokenSchedule brokenSchedule = new BrokenSchedule();
        brokenSchedule.setSchedule(weeklySchedule);
        brokenSchedule.setBreaks(holidayList);

        {
            System.out.println("Broken Schedule:");

            java.util.Date[] expected = new java.util.Date[]{
                    DateHelper.parseDateTime("2012-04-04 08:00"),
                    DateHelper.parseDateTime("2012-04-05 08:00"),
                    DateHelper.parseDateTime("2012-04-10 08:00"),
                    DateHelper.parseDateTime("2012-04-11 08:00"),};

            ScheduleContext context = new ScheduleContext(new Date());

            for (java.util.Date date : expected) {
                ScheduleResult result = brokenSchedule.nextDue(context);
                System.out.println("Next due: " + result.getFromDate());
                context = context.move(result.getUseNext());
                assertEquals(date, result.getFromDate());
            }
        }

        DailySchedule holidayTimes = new DailySchedule();
        holidayTimes.setAt("11:00");

        brokenSchedule.setAlternative(holidayTimes);

        {
            System.out.println("Alternate Schedule:");

            java.util.Date[] expected = new java.util.Date[]{
                    DateHelper.parseDateTime("2012-04-04 08:00"),
                    DateHelper.parseDateTime("2012-04-05 08:00"),
                    DateHelper.parseDateTime("2012-04-06 11:00"),
                    DateHelper.parseDateTime("2012-04-09 11:00"),
                    DateHelper.parseDateTime("2012-04-10 08:00"),
                    DateHelper.parseDateTime("2012-04-11 08:00"),};

            ScheduleContext context = new ScheduleContext(new Date());

            for (java.util.Date date : expected) {
                ScheduleResult result = brokenSchedule.nextDue(context);
                System.out.println("Next due: " + result.getFromDate());
                context = context.move(result.getUseNext());
                assertEquals(date, result.getFromDate());
            }
        }
    }

    @Test
    public void testTimerExample() throws InterruptedException {

        final long time = DateHelper.parseDateTime("2012-03-29 07:59:59.999")
                .getTime();

        class Date extends java.util.Date {
            private static final long serialVersionUID = 2012041200L;

            public Date() {
                super(time);
            }
        }

        String[] lines = null;

        TimerScheduler scheduler = new TimerScheduler(new Date());

        ConsoleCapture capture = new ConsoleCapture();
        try (ConsoleCapture.Close close = capture.captureConsole()) {

            scheduler.schedule(() -> System.out.println("Hello"));

            for (int i = 0; i < 10; ++i) {
                lines = capture.getLines();
                if (lines.length > 2) {
                    break;
                }
                Thread.sleep(500);
            }
        }

        assertTrue(lines.length > 2);

        assertEquals(
                "Scheduled at "
                        + DateHelper.parseDateTime("2012-03-29 08:00"), lines[0].trim());
        assertEquals("Hello", lines[1].trim());
        assertEquals(
                "Scheduled at "
                        + DateHelper.parseDateTime("2012-03-30 08:00"), lines[2].trim());

        scheduler.stop();
    }

    @Test
    public void testExecutorScheduler() throws
            InterruptedException {

        final long time = DateHelper.parseDateTime("2012-03-29 07:59:59.999")
                .getTime();

        class Date extends java.util.Date {
            private static final long serialVersionUID = 2012041200L;

            public Date() {
                super(time);
            }
        }

        ExecutorScheduler scheduler = new ExecutorScheduler(new Date());

        String[] lines = null;

        ConsoleCapture capture = new ConsoleCapture();
        try (ConsoleCapture.Close close = capture.captureConsole()) {

            scheduler.schedule(() -> System.out.println("Hello"));

            for (int i = 0; i < 10; ++i) {
                lines = capture.getLines();
                if (lines.length > 2) {
                    break;
                }
                Thread.sleep(500);
            }
        }

        assertTrue(lines.length > 2);

        assertEquals(
                "Scheduled at "
                        + DateHelper.parseDateTime("2012-03-29 08:00"), lines[0].trim());
        assertEquals("Hello", lines[1].trim());
        assertEquals(
                "Scheduled at "
                        + DateHelper.parseDateTime("2012-03-30 08:00"), lines[2].trim());

        scheduler.stop();
    }
}
