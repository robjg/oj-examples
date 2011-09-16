package org.oddjob.userguide;

import java.text.ParseException;

import junit.framework.TestCase;

import org.oddjob.OddjobDescriptorFactory;
import org.oddjob.OurDirs;
import org.oddjob.arooa.ArooaDescriptor;
import org.oddjob.arooa.ArooaParseException;
import org.oddjob.arooa.standard.StandardFragmentParser;
import org.oddjob.arooa.utils.DateHelper;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.schedules.IntervalTo;
import org.oddjob.schedules.Schedule;
import org.oddjob.schedules.ScheduleContext;
import org.oddjob.schedules.ScheduleResult;

public class SchedulesTest extends TestCase {

	Schedule create(String resource) throws ArooaParseException {
		
		OurDirs dirs = new OurDirs();
		
    	OddjobDescriptorFactory df = new OddjobDescriptorFactory();
    	
    	ArooaDescriptor descriptor = df.createDescriptor(
    			getClass().getClassLoader());
    	
    	StandardFragmentParser parser = new StandardFragmentParser(descriptor);
    	
    	parser.parse(new XMLConfiguration(
    			dirs.relative("examples/userguide/" + resource)));
    	
    	return (Schedule) parser.getRoot();
	}
	
	public void testSchedules1() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules1.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(DateHelper.parseDateTime("2010-03-16")));		
		
		assertEquals(new IntervalTo(
					DateHelper.parseDateTime("2010-03-16 00:00"),
					DateHelper.parseDateTime("2010-03-17 00:00")),
				result);
	}

	public void testSchedules2() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules2.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-16 10:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 00:00"),
				DateHelper.parseDateTime("2010-03-17 00:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 00:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-23 00:00"),
				DateHelper.parseDateTime("2010-03-24 00:00")),
			result);
	}
	
	public void testSchedules2a() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules2a.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-16 10:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 00:00"),
				DateHelper.parseDateTime("2010-03-17 00:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 00:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-23 00:00"),
				DateHelper.parseDateTime("2010-03-24 00:00")),
			result);
	}
	
	public void testSchedules3() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules3.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-16 10:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2009-10-01 00:00"),
				DateHelper.parseDateTime("2010-05-01 00:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-06-17 00:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-10-01 00:00"),
				DateHelper.parseDateTime("2011-05-01 00:00")),
			result);
	}
	
	public void testSchedules4() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules4.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-15 12:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 10:00"),
				DateHelper.parseDateTime("2010-03-16 13:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 09:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-23 10:00"),
				DateHelper.parseDateTime("2010-03-23 13:00")),
			result);
	}
	
	public void testSchedules5() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules5.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 00:59")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 22:00"),
				DateHelper.parseDateTime("2010-03-17 01:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 01:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-23 22:00"),
				DateHelper.parseDateTime("2010-03-24 01:00")),
			result);
	}
	
	public void testSchedules6() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules6.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-16 12:00:03")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 12:00:03"),
				DateHelper.parseDateTime("2010-03-16 12:00:08")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-16 12:00:09")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-16 12:00:09"),
				DateHelper.parseDateTime("2010-03-16 12:00:14")),
			result);
	}
	
	public void testSchedules7() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules7.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 01:03")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-17 01:00"),
				DateHelper.parseDateTime("2010-03-17 01:30")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 01:05")));
		
		// TODO: Not sure this is correct.
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-23 22:00"),
				DateHelper.parseDateTime("2010-03-23 22:30")),
			result);
	}
	
	public void testSchedules8() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules8.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-15 00:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-15 08:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 00:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-17 09:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2010-03-17 10:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2010-03-17 15:00")),
			result);
	}
	
	public void testSchedules9() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules9.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2011-12-05 11:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2011-12-05 00:00"),
				DateHelper.parseDateTime("2011-12-06 00:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2011-12-06 11:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2011-12-06 14:00")),
			result);
		
		result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2011-12-07 11:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2011-12-07 12:00")),
			result);
	}
	
	public void testSchedules10() throws ArooaParseException, ParseException {
		
		Schedule test = create("schedules10.xml");
		
		ScheduleResult result = test.nextDue(
				new ScheduleContext(
						DateHelper.parseDateTime("2011-04-05 11:00")));
		
		assertEquals(new IntervalTo(
				DateHelper.parseDateTime("2011-04-29 22:00"),
				DateHelper.parseDateTime("2011-04-30 00:00")),
			result);
	}
}
