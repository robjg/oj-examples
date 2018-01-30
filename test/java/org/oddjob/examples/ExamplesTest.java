package org.oddjob.examples;
import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.Setup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;
import org.oddjob.tools.OddjobTestHelper;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExamplesTest extends Assert {

	private static final Logger logger = LoggerFactory.getLogger(ExamplesTest.class);
	
	@Rule public TestName name = new TestName();

	ClassLoader classLoader;
	
	ArooaDescriptorFactory oddballs;
	
    @Before
    public void setUp() throws Exception {
	
    	logger.info("---------------------    " + name.getMethodName() + "   -----------------------");
    	
		Setup setup = new Setup();
		this.oddballs = setup.getOddballs();
		this.classLoader = setup.getClassLoader();
	}
	
    @Test
	public void testSimple() {

		Oddjob test = createOddjob("simple");
		
		test.run();
		
		assertEquals(ParentState.COMPLETE, OddjobTestHelper.getJobState(test));
		
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
	
	
    @Test
	public void testSchedule() throws ArooaConversionException {

		OurDirs dirs = new OurDirs();
		
		Oddjob test = new Oddjob();
		test.setDescriptorFactory(oddballs);
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(ParentState.COMPLETE, OddjobTestHelper.getJobState(test));
		
		Oddjob scheduling = new OddjobLookup(test).lookup(
				"scheduling", Oddjob.class); 
		
		scheduling.load();
		
		assertEquals(ParentState.READY, OddjobTestHelper.getJobState(
				scheduling));

		Oddjob dailyftp = new OddjobLookup(scheduling).lookup(
				"scheduling", Oddjob.class); 
		
		dailyftp.load();

		assertEquals(ParentState.READY, OddjobTestHelper.getJobState(dailyftp));
		
		test.destroy();
	}
		
    @Test
	public void testWorkflow() {

		OurDirs dirs = new OurDirs();
		
		Oddjob test = new Oddjob();
		test.setClassLoader(classLoader);
		test.setFile(new File(dirs.base(), "examples/oddjob.xml"));
		test.run();
		
		assertEquals(ParentState.COMPLETE, OddjobTestHelper.getJobState(test));
		
		Oddjob workflow = (Oddjob) new OddjobLookup(test).lookup("workflow");
		
		workflow.load();
				
		assertEquals(ParentState.READY, OddjobTestHelper.getJobState(
				workflow));
		
		test.destroy();
	}
	
}
