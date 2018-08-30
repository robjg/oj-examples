package org.oddjob.cmdln;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.Oddjob;
import org.oddjob.OddjobLookup;
import org.oddjob.OjTestCase;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.logging.Appender;
import org.oddjob.arooa.logging.AppenderAdapter;
import org.oddjob.arooa.logging.LogLevel;
import org.oddjob.arooa.logging.LoggerAdapter;
import org.oddjob.arooa.logging.LoggingEvent;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.jobs.ExecJob;
import org.oddjob.logging.slf4j.LogoutType;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecExamplesRunningOddjobTest {

	private static final Logger logger = LoggerFactory.getLogger(ExecExamplesRunningOddjobTest.class);
	
	// Do not use oddjob.run.jar as it's set by the Launcher test if it runs first.
	static final String ODDJOB_RUN_JAR_PROPERTY = "oddjob.test.run.jar";
	
	@Rule public TestName name = new TestName();

	@Before
	public void setUp() {
        logger.info("---------------------  " + name.getMethodName() + "  -------------------");
    }

    @Test
	public void testWithStdInExample() throws ArooaPropertyException, ArooaConversionException, IOException {
		
		File oddjobDir = new OurDirs().relative("../oddjob");
		File runJar = new File(oddjobDir, "run-oddjob.jar");
		File logConfig = new File(oddjobDir, OjTestCase.logConfig());
		
		logger.info("Setting {} to {}", ODDJOB_RUN_JAR_PROPERTY, runJar.getCanonicalPath());
		
		Properties properties = new Properties();
		properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar.getCanonicalPath());
		properties.put("logConfigArgs", "-l " + logConfig.toString());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(oddjobDir,
				"src/test/resources/org/oddjob/jobs/ExecWithStdInExample.xml"));
		oddjob.setProperties(properties);
		
		oddjob.load();
		
		ExecJob exec = new OddjobLookup(oddjob).lookup("exec", ExecJob.class);
		
		ConsoleCapture console = new ConsoleCapture();
		console.setLeaveLogging(true);
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
			
			oddjob.run();
		}
		
		console.dump(logger);
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
		String[] lines = console.getLines();
		
		assertEquals("apples", lines[0].trim());
		assertEquals("oranges", lines[1].trim());
		assertEquals("pears", lines[2].trim());
		
		assertEquals(3, lines.length);
		
		oddjob.destroy();
	}

	
    @Test
	public void testWithRedirectToFileExample() throws ArooaPropertyException, ArooaConversionException, IOException {
		
		File oddjobDir = new OurDirs().relative("../oddjob");
		File runJar = new File(oddjobDir, "run-oddjob.jar");
		File workDir = new File( oddjobDir, "work");
		File output = new File(workDir, "ExecOutput.log");
		if (output.exists()) {
			output.delete();
		}
		
		Properties properties = new Properties();
		properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar.getCanonicalPath());
		properties.put("work.dir", workDir.getCanonicalPath());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(oddjobDir,
				"src/test/resources/org/oddjob/jobs/ExecWithRedirectToFile.xml"));
		oddjob.setProperties(properties);

		oddjob.load();
		
		ExecJob exec = new OddjobLookup(oddjob).lookup("exec", ExecJob.class);
		
		ConsoleCapture console = new ConsoleCapture();
		console.setLeaveLogging(true);
		
		try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {
			
			oddjob.run();
		}
		
		console.dump(logger);
		
		assertEquals(ParentState.COMPLETE, 
				oddjob.lastStateEvent().getState());
		
			
		oddjob.destroy();
	}
	
	
    @Test
	public void testWithRedirectToLogExample() throws IOException {
		
		File oddjobDir = new OurDirs().relative("../oddjob");
		File runJar = new File(oddjobDir, "run-oddjob.jar");
		File logConfig = new File(oddjobDir, OjTestCase.logConfig());
		
		Properties properties = new Properties();
		properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar.getCanonicalPath());
		properties.put("logConfigArgs", "-l " + logConfig.getCanonicalPath());
		
		Oddjob oddjob = new Oddjob();
		oddjob.setFile(new File(oddjobDir,
				"src/test/resources/org/oddjob/jobs/ExecWithRedirectToLog.xml"));
		oddjob.setProperties(properties);
		
		Results results = new Results();
		
		AppenderAdapter appenderAdapter = LoggerAdapter.appenderAdapterFor(LogoutType.class);
		
		appenderAdapter.addAppender(results);
		
		oddjob.run();
				
		appenderAdapter.removeAppender(results);
		
		assertEquals(ParentState.INCOMPLETE,
				oddjob.lastStateEvent().getState());
		
		assertTrue(results.info.size() == 0);		
		assertTrue(results.warn.size() > 0);		
		
		oddjob.destroy();		
	}
		
	private static class Results implements Appender {
		
		List<Object> info = new ArrayList<Object>();
		List<Object> warn = new ArrayList<Object>();
		
		@Override
		public void append(LoggingEvent arg0) {
			if (arg0.getLevel() == LogLevel.INFO) {
				info.add(arg0.getMessage());
			}
			if (arg0.getLevel() == (LogLevel.WARN)) {
				warn.add(arg0.getMessage());
			}
		}
	}
}
