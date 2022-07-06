package org.oddjob;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.oddjob.jobs.ExecJob;
import org.oddjob.state.JobState;
import org.oddjob.state.ParentState;
import org.oddjob.state.StateEvent;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OddjobTestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class OddjobCommandLineTest {
    private static final Logger logger = LoggerFactory.getLogger(
            OddjobCommandLineTest.class);

    @Rule
    public TestName name = new TestName();

    /**
     * The oddjob project dir
     */
    File oddjobHome;

    String runJar;

    @Before
    public void setUp() throws Exception {
        logger.info("---------------- " + name.getMethodName() + " -----------------");

        this.oddjobHome = OddjobSrc.oddjobSrc().toFile();

        Path runJarPath = OddjobSrc.appJar();

        assertThat("File exists " + runJarPath,
                Files.exists(runJarPath), is(true));

        this.runJar = runJarPath.toString();
    }

    String relative(String fileName) {
        try {
            File file = new File(oddjobHome, fileName);
            assertThat("File exists " + file,
                    file.exists(), is(true));
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOddjobFailsNoFile() {

        String fileName = "Idontexist.xml";

        File file = new File(fileName);
        //noinspection ResultOfMethodCallIgnored
        file.delete();

        ExecJob exec = new ExecJob();
        exec.setCommand("java -jar " + runJar +
                " -f " + fileName +
                " -l " + relative("test/launch/logback.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {

            exec.run();
        }

        console.dump();

        // File created but Oddjob ready, not complete so exit status
        // is incomplete.
        assertThat(exec.lastStateEvent().getState(), is(JobState.INCOMPLETE));

        String[] lines = console.getLines();
        assertThat(lines[0], Matchers.startsWith("Exception"));

        exec.destroy();
    }

    @Test
    public void testClientServer() throws InterruptedException, FailedToStopException {

        OurDirs dirs = new OurDirs();

        ExecJob exec = new ExecJob();
        exec.setCommand("java -jar " + runJar +
                " -f " + relative("server.xml") +
                " -l " + dirs.relative("../arooa/src/test/resources/logback-test.xml"));

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
                } else {
                    logger.info("Client state " + event.getState());
                    break;
                }
            }

            assertThat(oddjob.lastStateEvent().getState(), is(ParentState.STARTED));

            Object client = new OddjobLookup(oddjob).lookup("client");

            Object[] serverJobs = OddjobTestHelper.getChildren(client);

            assertThat(serverJobs.length, is(1));

            assertThat(serverJobs[0].toString(), is("Server Jobs"));

            oddjob.onDestroy();

            exec.stop();
        }

        console.dump();
    }


    @Test
    public void testVenkyParallel() {

        ExecJob exec = new ExecJob();
        exec.setCommand("java -jar " + runJar +
                " -f " + new OurDirs().relative("src/test/config/testflow2.xml") +
                " -l " + relative("test/launch/logback.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {

            exec.run();
        }

        console.dump();

        assertThat(exec.getExitValue(), is(0));

        String[] lines = console.getLines();

        assertThat(lines.length, is(2));

        // Parallel could produce either order and even interleave the two outputs so a and b are
        // on the same line.
        String results = lines[0] + lines[1];

        assertThat(results, Matchers.containsString("a"));
        assertThat(results, Matchers.containsString("b"));
    }

    /**
     * Because stop wasn't stopping mirror job, errors were logged. At the
     * moment we have no way of capturing listener exceptions except via the
     * log, and it's quite good to have a few complete tests anyway.
     */
    @Test
    public void testDestroyNoErrors() {

        ExecJob exec = new ExecJob();
        exec.setCommand("java -jar " + runJar +
                " -f " + new OurDirs().relative("src/test/config/sequential-with-mirror.xml") +
                " -l " + relative("test/launch/logback.xml"));

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close close = console.capture(exec.consoleLog())) {

            exec.run();
        }

        console.dump();

        assertThat(exec.getExitValue(), is(1));

        String[] lines = console.getLines();
        assertThat(lines.length, is(1));

        assertThat(lines[0].trim(), is("This will always run."));
    }

    @Test
    public void testMBeanClientServer()
            throws FailedToStopException, InterruptedException {

        File testDir = new File(oddjobHome, "src/test/resources/org/oddjob/jmx");

        File configFile = new File(testDir, "PlatformMBeanServerExample.xml");
        assertThat(configFile.exists(), is(true));


        ExecJob serverExec = new ExecJob();
        serverExec.setCommand("java " +
                "-Dcom.sun.management.jmxremote.port=13013 " +
                "-Dcom.sun.management.jmxremote.ssl=false " +
                "-Dcom.sun.management.jmxremote.authenticate=false " +
                "-jar " + runJar +
                " -f " + configFile +
                " -l " + relative("test/launch/logback.xml"));

        ConsoleCapture serverConsole = new ConsoleCapture();
        try (ConsoleCapture.Close close = serverConsole.capture(serverExec.consoleLog())) {

            Thread t = new Thread(serverExec);
            t.start();

            for (int i = 0; i < 10; ++i) {
                serverConsole.dump();
                if (serverConsole.size() > 0) {
                    break;
                }
                Thread.sleep(1000);
            }

            String[] lines = serverConsole.getLines();

            assertThat(lines[0].trim(), is("Hello from an Oddjob Server!"));
            assertThat(lines.length, is(1));

            ExecJob clientExec = new ExecJob();
            clientExec.setCommand("java -jar " + runJar +
                    " -f " + new File(testDir, "PlatformMBeanClientExample.xml") +
                    " -l " + relative("test/launch/logback.xml") +
                    " localhost:13013");

            ConsoleCapture clientConsole = new ConsoleCapture();
            try (ConsoleCapture.Close close2 = clientConsole.capture(clientExec.consoleLog())) {

                clientExec.run();
            }

            clientConsole.dump(logger);

            lines = clientConsole.getLines();

            assertThat(lines[0].trim(), is("Hello from an Oddjob Server!"));
            assertThat(lines.length, is(1));

        } finally {
            serverExec.stop();
        }
    }
}
