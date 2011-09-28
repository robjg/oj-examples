package org.oddjob.examples;

import java.io.File;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.Setup;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.state.ParentState;

public class ReferenceSqlTest extends TestCase {

	File dir;

	ClassLoader classLoader;
	
	ArooaDescriptorFactory oddballs;
	
	@Override
	protected void setUp() throws Exception {
		
		dir = new File(new OurDirs().base(), 
				"examples/reference");
		
		assertTrue(dir.exists());
		
		Setup setup = new Setup();
		this.oddballs = setup.getOddballs();
		this.classLoader = setup.getClassLoader();
	}
	
	public void testSqlExample() throws Exception {
		
		Oddjob oddjob = new Oddjob();
		oddjob.setDescriptorFactory(oddballs);
		oddjob.setClassLoader(classLoader);
		oddjob.setFile(new File(dir, "job.sql.xml"));
		
		oddjob.run();

		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
/*		
		OddjobLookup lookup = new OddjobLookup(oddjob);
		
		Runnable top = lookup.lookup("top", Runnable.class);
		
		top.run();
		
		Runnable setup = lookup.lookup("setup", Runnable.class);
		
		setup.run();
		
		Runnable single = lookup.lookup("single-query", Runnable.class);
		
		single.run();
		
		String result = lookup.lookup("result.text", String.class);
		
		assertEquals("hello", result);
		
		Runnable cleanup = lookup.lookup("clean-up", Runnable.class);
		
		cleanup.run();
		
		((Stoppable) top).stop();
*/		
		oddjob.destroy();
	}	
}
