package org.oddjob.devguide;

import java.io.File;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.state.JobState;

import junit.framework.TestCase;

public class IntroducingArooaTest extends TestCase {

	File dir;
	
	@Override
	protected void setUp() throws Exception {
		
		dir = new File(
				new OurDirs().base(), "examples/devguide");
		
		assertTrue(dir.exists());
	}
	
	public void testArooa1() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());
		
		oj.destroy();
	}
	
	public void testArooa2() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component2.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());
		
		oj.destroy();
	}
	
	public void testArooa3() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "component3.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());
		
		oj.destroy();
	}
	
	public void testArooa4() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "properties.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());

		oj.destroy();
	}
	
	public void testArooa5() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "properties2.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());

		oj.destroy();
	}

	public void testHelloPerson() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "helloperson.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());

		oj.destroy();
	}
	
	public void testHelloPerson2() {
		
		Oddjob oj = new Oddjob();
		oj.setFile(new File(dir, "helloperson-main.xml"));
		oj.run();

		assertEquals(JobState.COMPLETE, oj.lastJobStateEvent().getJobState());

		oj.destroy();
	}
}
