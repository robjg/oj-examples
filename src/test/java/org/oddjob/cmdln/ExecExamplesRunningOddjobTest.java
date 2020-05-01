package org.oddjob.cmdln;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.*;
import org.oddjob.arooa.convert.ArooaConversionException;
import org.oddjob.arooa.logging.*;
import org.oddjob.arooa.reflect.ArooaPropertyException;
import org.oddjob.jobs.ExecJob;
import org.oddjob.logging.slf4j.LogoutType;
import org.oddjob.state.ParentState;
import org.oddjob.tools.ConsoleCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ExecExamplesRunningOddjobTest {

    private static final Logger logger = LoggerFactory.getLogger(ExecExamplesRunningOddjobTest.class);

    // Do not use oddjob.run.jar as it's set by the Launcher test if it runs first.
    static final String ODDJOB_RUN_JAR_PROPERTY = "oddjob.test.run.jar";

    @Rule
    public TestName name = new TestName();

    private Path oddjobDir = OddjobSrc.oddjobSrc();

    private Path runJar = OddjobSrc.appJar();

    @Before
    public void setUp() {
        logger.info("---------------------  " + name.getMethodName() + "  -------------------");
    }

    @Test
    public void testWithStdInExample() throws ArooaPropertyException, ArooaConversionException {

        Path logConfig = oddjobDir.resolve(OjTestCase.logConfig());

        logger.info("Setting {} to {}", ODDJOB_RUN_JAR_PROPERTY, runJar);

        Properties properties = new Properties();
        properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar.toString());
        properties.put("logConfigArgs", "-l " + logConfig.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(oddjobDir.resolve(
                "src/test/resources/org/oddjob/jobs/ExecWithStdInExample.xml").toFile());
        oddjob.setProperties(properties);

        oddjob.load();

        ExecJob exec = new OddjobLookup(oddjob).lookup("exec", ExecJob.class);

        ConsoleCapture console = new ConsoleCapture();
        console.setLeaveLogging(true);
        try (ConsoleCapture.Close ignored = console.capture(exec.consoleLog())) {

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

        Path workDir = OurDirs.workPathDir(getClass().getSimpleName(), true);
        Path output = workDir.resolve("ExecOutput.log");

        Properties properties = new Properties();
        properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar);
        properties.put("work.dir", workDir.toString());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(oddjobDir.resolve(
                "src/test/resources/org/oddjob/jobs/ExecWithRedirectToFile.xml").toFile());
        oddjob.setProperties(properties);

        oddjob.load();

        ExecJob exec = new OddjobLookup(oddjob).lookup("exec", ExecJob.class);

        ConsoleCapture console = new ConsoleCapture();
        console.setLeaveLogging(true);

        try (ConsoleCapture.Close ignored = console.capture(exec.consoleLog())) {

            oddjob.run();
        }

        console.dump(logger);

        assertEquals(ParentState.COMPLETE,
                oddjob.lastStateEvent().getState());

		assertThat(Files.exists(output), is(true));

        oddjob.destroy();
    }


    @Test
    public void testWithRedirectToLogExample() throws IOException {

        File logConfig = oddjobDir.resolve(OjTestCase.logConfig())
                .toFile();

        Properties properties = new Properties();
        properties.put(ODDJOB_RUN_JAR_PROPERTY, runJar.toString());
        properties.put("logConfigArgs", "-l " + logConfig.getCanonicalPath());

        Oddjob oddjob = new Oddjob();
        oddjob.setFile(oddjobDir.resolve(
                "src/test/resources/org/oddjob/jobs/ExecWithRedirectToLog.xml").toFile());
        oddjob.setProperties(properties);

        Results results = new Results();

        AppenderAdapter appenderAdapter = LoggerAdapter.appenderAdapterFor(LogoutType.class);

        appenderAdapter.addAppender(results);

        oddjob.run();

        appenderAdapter.removeAppender(results);

        assertEquals(ParentState.INCOMPLETE,
                oddjob.lastStateEvent().getState());

        assertEquals(0, results.info.size());
        assertTrue(results.warn.size() > 0);

        long lines = results.warn.stream()
                .filter(m -> m.contains("java.io.FileNotFoundException: Missing.xml"))
                .count();

        assertTrue(lines > 0);

        oddjob.destroy();
    }

    private static class Results implements Appender {

        List<String> info = new ArrayList<>();
        List<String> warn = new ArrayList<>();

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
