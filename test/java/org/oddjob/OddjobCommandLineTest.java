package org.oddjob;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import org.junit.Assert;

import org.apache.log4j.Logger;
import org.oddjob.jobs.ExecJob;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.state.StateEvent;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OddjobSrc;
import org.oddjob.tools.OddjobTestHelper;
import org.oddjob.tools.OurDirs;

public class OddjobCommandLineTest extends Assert {
	private static final Logger logger = Logger.getLogger(
			OddjobCommandLineTest.class);
	
	final static String RUN_JAR = "run-oddjob.jar";
	
	final static String EOL = System.getProperty("line.separator");
	
	@Rule public TestName name = new TestName();

	/** The oddjob project dir */
	File oddjobHome;
		
    @Before
    public void setUp() throws Exception {
		logger.info("---------------- " + name.getMethodName() + " -----------------");
		
		this.oddjobHome = new OddjobSrc().oddjobSrcBase();		
		File runJar = new File(this.oddjobHome, RUN_JAR);

		
		assertTrue(runJar.exists());		
	}
	
	String relative(String fileName) {
		try {
			return new File(oddjobHome, fileName).getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
    @Test
	public void testOddjobFailsNoFile() throws InterruptedException {
		
		String fileName = "Idontexist.xml";
		
		File file = new File(fileName);
		file.delete();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + relative(RUN_JAR) + 
				" -f " + fileName);
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
			
			exec.run();
		}
		
		console.dump();
		
		// File created but Oddjob ready, not complete so exit status
		// is incomplete.
		assertEquals(JobState.INCOMPLETE, exec.lastStateEvent().getState());

		String[] lines = console.getLines();
		assertTrue(lines[0].startsWith("Exception"));
		
		exec.destroy();
	}
	
    @Test
	public void testClientServer() throws InterruptedException, FailedToStopException {
		
		OurDirs dirs = new OurDirs();
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + relative(RUN_JAR) +  
				" -f " + relative("server.xml") +
				" -l " + dirs.relative("../arooa/test/java/log4j.properties"));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
		
			Thread t = new Thread(exec);
			t.start();
			
			Oddjob oddjob = new Oddjob();
			oddjob.setFile(new File(relative("client.xml")));
	
			for (int i = 0; i < 10; ++i) {
				console.dump();
				oddjob.softReset();
				oddjob.run();
				StateEvent event = oddjob.lastStateEvent(); 
				if (event.getState() 
						== ParentState.EXCEPTION) {
					logger.info("Client Oddjob Exception", event.getException());
					Thread.sleep(2000);
				}
				else {
					logger.info("Client state " + event.getState());
					break;
				}
			} 

			assertEquals(ParentState.STARTED, 
					oddjob.lastStateEvent().getState());
			
			Object client = new OddjobLookup(oddjob).lookup("client");
			
			Object[] serverJobs = OddjobTestHelper.getChildren((Structural) client); 
			
			assertEquals(1, serverJobs.length);
			
			assertEquals("Server Jobs", serverJobs[0].toString());
			
			oddjob.onDestroy();
			
			exec.stop();
		}
		
		console.dump();
	}
	

    @Test
	public void testVenkyParallel() throws InterruptedException {
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + relative(RUN_JAR) + 
				" -f " + new OurDirs().relative("test/config/testflow2.xml"));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
			
			exec.run();
		}
		
		assertEquals(0, exec.getExitValue());
		
		console.dump();
		
		String[] lines = console.getLines();
		
		assertEquals(2, lines.length);

		// Parallel could produce either order.
		HashSet<String> results = new HashSet<String>();

		results.add(lines[0]);
		results.add(lines[1]);
		
		assertTrue(results.contains("a" + EOL));
		assertTrue(results.contains("b" + EOL));
	}

	/**
	 * Because stop wasn't stopping mirror job, errors were logged. At the
	 * moment we have no way of capturing listener exceptions except via the
	 * log, and it's quite good to have a few complete tests anyway.
	 * 
	 * @throws InterruptedException
	 */
    @Test
	public void testDestroyNoErrors() throws InterruptedException {
		
		ExecJob exec = new ExecJob();
		exec.setCommand("java -jar " + relative(RUN_JAR) + 
				" -f " + new OurDirs().relative("test/config/sequential-with-mirror.xml"));
		
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
			
			exec.run();
		}
		
		console.dump();
		
		assertEquals(1, exec.getExitValue());
		
		String[] lines = console.getLines();
		assertEquals(1, lines.length);

		assertEquals("This will always run.", lines[0].trim());		
	}
	
	
    @Test
	public void testMBeanClientServer() 
	throws FailedToStopException, InterruptedException {
		
		File testDir = new File(oddjobHome, "test/java/org/oddjob/jmx");
		
		ExecJob serverExec = new ExecJob();
		serverExec.setCommand("java " +
				"-Dcom.sun.management.jmxremote.port=13013 " +
				"-Dcom.sun.management.jmxremote.ssl=false " +
				"-Dcom.sun.management.jmxremote.authenticate=false " +
				"-jar " + relative(RUN_JAR) +  
				" -f " + new File(testDir, "PlatformMBeanServerExample.xml"));

		ConsoleCapture serverConsole = new ConsoleCapture();
		try (ConsoleCapture.Close close = serverConsole.capture(serverExec.consoleLog())) {

			Thread t = new Thread(serverExec);
			t.start();

			for (int i = 0; i < 10; ++i) {
				serverConsole.dump();
				if (serverConsole.size() == 1) {
					continue;
				}
				Thread.sleep(1000);
			} 

			String[] lines = serverConsole.getLines();

			assertEquals("Hello from an Oddjob Server!", lines[0].trim());
			assertEquals(1, lines.length);

			ExecJob clientExec = new ExecJob();
			clientExec.setCommand("java -jar " + relative(RUN_JAR) +  
					" -f " + new File(testDir, "PlatformMBeanClientExample.xml localhost:13013"));

			ConsoleCapture clientConsole = new ConsoleCapture();
			try (ConsoleCapture.Close close2 = clientConsole.capture(clientExec.consoleLog())) {

				clientExec.run();
			}

			clientConsole.dump(logger);

			lines = clientConsole.getLines();

			assertEquals("Hello from an Oddjob Server!", lines[0].trim());
			assertEquals(1, lines.length);
		
		}
		
		serverExec.stop();
	}
	
}
