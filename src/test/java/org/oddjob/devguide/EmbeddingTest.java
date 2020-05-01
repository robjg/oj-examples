package org.oddjob.devguide;

import org.junit.Test;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.oddjob.state.ParentState;

public class EmbeddingTest extends Assert {

    @Test
	public void testSimple() throws ArooaPropertyException, ArooaConversionException {

		// #first{
		
		String xml = 
			"<oddjob>" +
			" <job>" +
			"  <echo id='hello-job'>Hello World</echo>" +
			" </job>" +
			"</oddjob>";
		
		Oddjob oddjob = new Oddjob();
		oddjob.setConfiguration(new XMLConfiguration("EMBEDDED XML", xml));
	
		oddjob.run();
		
		// }#first
		
		assertEquals(ParentState.COMPLETE, oddjob.lastStateEvent().getState());
		
		// #second {
		
		String greeting = new OddjobLookup(oddjob).lookup("hello-job.text", String.class);
		
		// } #second
		
		assertEquals("Hello World", greeting);

		// #last {
		
		oddjob.destroy();		
		
		// } #last
	}
	
}
