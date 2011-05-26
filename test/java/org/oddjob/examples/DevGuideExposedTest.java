package org.oddjob.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.devguide.StandardAPExample;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogLevel;
import org.oddjob.logging.LogListener;

public class DevGuideExposedTest extends TestCase {

	File dir;
	
	@Override
	protected void setUp() throws Exception {
		
		dir = new File(new OurDirs().base(), "examples/devguide");
		
		assertTrue(dir.exists());
	}
	
	class LL implements LogListener {
		List<String> messages = new ArrayList<String>();
		
		public void logEvent(LogEvent logEvent) {
			messages.add(logEvent.getMessage());
		}
		
	}
	
	String EOL = System.getProperty("line.separator");
	
	public void testStandardAPExample() throws Exception {

		LL ll = new LL();
		
		Oddjob.CONSOLE.addListener(ll, LogLevel.INFO, -1, 0);
		
		StandardAPExample.main(new String[] {
				new File(dir, "exposed1.xml").getCanonicalPath()
		});

		Oddjob.CONSOLE.removeListener(ll);

		assertEquals("Person Set." + EOL, ll.messages.get(0));
		
	}
	
}
