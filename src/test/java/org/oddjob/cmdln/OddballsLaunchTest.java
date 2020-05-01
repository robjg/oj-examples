package org.oddjob.cmdln;

import org.junit.Before;
import org.junit.Test;
import org.oddjob.OddjobSrc;
import org.oddjob.OjTestCase;
import org.oddjob.jobs.ExecJob;
import org.oddjob.tools.ConsoleCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class OddballsLaunchTest extends OjTestCase {
	private static final Logger logger = LoggerFactory.getLogger(
			OddballsLaunchTest.class);

	File oddjobDir = OddjobSrc.oddjobSrc().toFile();
	
	File runOddjob = OddjobSrc.appJar().toFile();
	
    @Before
    public void setUp() {
		logger.info("---------------- " + getName() + " -----------------");
		
		assertTrue(runOddjob.exists());
	}
	
    @Test
	public void testOddjobFailsNoFile() {
		

	    File oddballDir = new File(oddjobDir, "/test/oddballs");
	    File configFile = new File(oddjobDir, "/test/launch/oddballs-launch.xml");
		File logConfig = new File(oddjobDir, OjTestCase.logConfig());
	    
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar \"" + runOddjob + "\"" + 
				" -ob \"" + oddballDir + "\"" +
				" -f \"" + configFile + "\"" +
				" -l \"" + logConfig + "\"");

		ConsoleCapture capture = new ConsoleCapture();
		try (ConsoleCapture.Close ignored = capture.capture(exec.consoleLog())) {
			exec.run();
		}
		
		capture.dump();

		String[] lines = capture.getLines();
		
		assertEquals("Apple: Red", lines[0]);
		assertEquals("Colour As String: Red", lines[1]);
		
		exec.destroy();
	}

}
