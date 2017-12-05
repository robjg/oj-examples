package org.oddjob.examples;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oddjob.OddjobConsole;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.devguide.StandardAPExample;
import org.oddjob.logging.LogArchive;
import org.oddjob.logging.LogEvent;
import org.oddjob.logging.LogListener;
import org.oddjob.tools.OurDirs;

public class DevGuideExposedTest extends Assert {

	File dir;
	
    @Before
    public void setUp() throws Exception {
		
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
	
    @Test
	public void testStandardAPExample() throws Exception {

		LL ll = new LL();
		
		try ( OddjobConsole.Close oddjobConsoleClose = OddjobConsole.initialise()) {
			
			LogArchive console = OddjobConsole.console();
			
			console.addListener(ll, LogLevel.INFO, -1, 0);
			
			StandardAPExample.main(new String[] {
					new File(dir, "exposed1.xml").getCanonicalPath()
			});

			console.removeListener(ll);
		}

		assertEquals("Person Set." + EOL, ll.messages.get(0));
		
	}
	
}
