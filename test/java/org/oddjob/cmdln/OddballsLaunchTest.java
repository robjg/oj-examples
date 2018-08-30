package org.oddjob.cmdln;
import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.oddjob.OjTestCase;
import org.oddjob.jobs.ExecJob;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OddballsLaunchTest extends OjTestCase {
	private static final Logger logger = LoggerFactory.getLogger(
			OddballsLaunchTest.class);
	
	final static String RUN_JAR = "run-oddjob.jar";

	File oddjobDir = new OurDirs().relative("../oddjob");
	
	File runOddjob = new File(oddjobDir, RUN_JAR);
	
    @Before
    public void setUp() throws Exception {
		logger.info("---------------- " + getName() + " -----------------");
		
		assertTrue(runOddjob.exists());
	}
	
    @Test
	public void testOddjobFailsNoFile() throws InterruptedException {
		

	    File oddballDir = new File(oddjobDir, "/test/oddballs");
	    File configFile = new File(oddjobDir, "/test/launch/oddballs-launch.xml");
		File logConfig = new File(oddjobDir, OjTestCase.logConfig());
	    
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar \"" + runOddjob + "\"" + 
				" -ob \"" + oddballDir + "\"" +
				" -f \"" + configFile + "\"" +
				" -l \"" + logConfig + "\"");

		ConsoleCapture capture = new ConsoleCapture();
		try (ConsoleCapture.Close close = capture.capture(exec.consoleLog())) {
			exec.run();
		}
		
		capture.dump();

		String[] lines = capture.getLines();
		
		assertEquals("Apple: Red", lines[0]);
		assertEquals("Colour As String: Red", lines[1]);
		
		exec.destroy();
	}
	
	
	void dump(List<String> lines) {
		System.out.println("******************");
		for (String line : lines) {
			System.out.print(line);
		}
		System.out.println("******************");
	}
	
}
