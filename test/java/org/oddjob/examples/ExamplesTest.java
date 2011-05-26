package org.oddjob.examples;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Helper;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.Setup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.oddballs.OddballsDirDescriptorFactory;
import org.oddjob.state.JobState;


public class ExamplesTest extends TestCase {

	ClassLoader classLoader;
	
	File oddballs;
	
	@Override
	protected void setUp() throws Exception {
		
		Setup setup = new Setup();
		this.oddballs = setup.getOddballs();
		this.classLoader = setup.getClassLoader();
	}
	
	public void testSimple() {

		Oddjob test = createOddjob("simple");
		
		test.run();
		
		assertEquals(JobState.COMPLETE, Helper.getJobState(test));
		
		test.destroy();
	}
	
	public void testReference() {

		Oddjob test = createOddjob("reference");
		
		test.setClassLoader(classLoader);
		test.setDescriptorFactory(
				new OddballsDirDescriptorFactory(oddballs));
		test.run();
		
		assertEquals(JobState.COMPLETE, Helper.getJobState(test));
		
		Object reference = new OddjobLookup(test).lookup(
				"examples/reference");
		
		assertEquals(JobState.COMPLETE, Helper.getJobState(
				reference));
		
		test.destroy();
	}
	
	Oddjob createOddjob(String example) {
		
		OurDirs dirs = new OurDirs();
		
		String xml = 
			"<oddjob>" +
			"  <job>" +
			"    <sequential>" +
			"      <jobs>" +
			"        <oddjob id='examples'" +
			"                file='" + dirs.base() + "/examples/oddjob.xml'/>" +
			"        <run job='${examples/" + example + "}'/>" +
			"     </jobs>" +
			"   </sequential>" +
			"  </job>" +
			"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("XML", xml));
		
		return oddjob;
	}
	
	
	public void testSchedule() throws ArooaConversionException {

		OurDirs dirs = new OurDirs();
		
		Oddjob test = new Oddjob();
		test.setDescriptorFactory(
				new OddballsDirDescriptorFactory(oddballs));
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(JobState.COMPLETE, Helper.getJobState(test));
		
		Oddjob scheduling = new OddjobLookup(test).lookup(
				"scheduling", Oddjob.class); 
		
		scheduling.load();
		
		assertEquals(JobState.READY, Helper.getJobState(
				scheduling));

		Oddjob dailyftp = new OddjobLookup(scheduling).lookup(
				"dailyftp", Oddjob.class); 
		
		dailyftp.load();

		assertEquals(JobState.READY, Helper.getJobState(dailyftp));
		
		test.destroy();
	}
		
	public void testWorkflow() {

		OurDirs dirs = new OurDirs();
		
		Oddjob test = new Oddjob();
		test.setClassLoader(classLoader);
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(JobState.COMPLETE, Helper.getJobState(test));
		
		Oddjob workflow = (Oddjob) new OddjobLookup(test).lookup("workflow");
		
		workflow.load();
				
		assertEquals(JobState.READY, Helper.getJobState(
				workflow));
		
		test.destroy();
	}
	
}
