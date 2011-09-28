package org.oddjob.examples;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Helper;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OurDirs;
import org.oddjob.Setup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;


public class ExamplesTest extends TestCase {

	ClassLoader classLoader;
	
	ArooaDescriptorFactory oddballs;
	
	@Override
	protected void setUp() throws Exception {
		
		Setup setup = new Setup();
		this.oddballs = setup.getOddballs();
		this.classLoader = setup.getClassLoader();
	}
	
	public void testSimple() {

		Oddjob test = createOddjob("simple");
		
		test.run();
		
		assertEquals(ParentState.COMPLETE, Helper.getJobState(test));
		
		test.destroy();
	}
	
	public void testReference() {

		Oddjob test = createOddjob("reference");
		
		test.setClassLoader(classLoader);
		test.setDescriptorFactory(oddballs);
		test.run();
		
		assertEquals(ParentState.COMPLETE, test.lastStateEvent().getState());
		
		Object reference = new OddjobLookup(test).lookup(
				"examples/reference");
		
		assertEquals(ParentState.COMPLETE, Helper.getJobState(
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
		test.setDescriptorFactory(oddballs);
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(ParentState.COMPLETE, Helper.getJobState(test));
		
		Oddjob scheduling = new OddjobLookup(test).lookup(
				"scheduling", Oddjob.class); 
		
		scheduling.load();
		
		assertEquals(ParentState.READY, Helper.getJobState(
				scheduling));

		Oddjob dailyftp = new OddjobLookup(scheduling).lookup(
				"dailyftp", Oddjob.class); 
		
		dailyftp.load();

		assertEquals(ParentState.READY, Helper.getJobState(dailyftp));
		
		test.destroy();
	}
		
	public void testWorkflow() {

		OurDirs dirs = new OurDirs();
		
		Oddjob test = new Oddjob();
		test.setClassLoader(classLoader);
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(ParentState.COMPLETE, Helper.getJobState(test));
		
		Oddjob workflow = (Oddjob) new OddjobLookup(test).lookup("workflow");
		
		workflow.load();
				
		assertEquals(ParentState.READY, Helper.getJobState(
				workflow));
		
		test.destroy();
	}
	
}
