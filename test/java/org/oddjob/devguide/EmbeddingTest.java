package org.oddjob.devguide;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.JobState;

public class EmbeddingTest extends TestCase {

	public void testSimple() throws ArooaPropertyException, ArooaConversionException {

		// #first{
		
		String xml = 
			"<oddjob>" +
			" <job>" +
			"  <echo id='hello-job' text='Hello World'/>" +
			" </job>" +
			"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("EMBEDDED XML", xml));
	
		oddjob.run();
		
		// }#first
		
		assertEquals(JobState.COMPLETE, oddjob.lastJobStateEvent().getJobState());
		
		// #second {
		
		String greeting = new OddjobLookup(oddjob).lookup("hello-job.text", String.class);
		
		// } #second
		
		assertEquals("Hello World", greeting);

		// #last {
		
		oddjob.destroy();		
		
		// } #last
	}
	
}
