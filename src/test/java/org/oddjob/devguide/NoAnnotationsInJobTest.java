package org.oddjob.devguide;

import org.junit.Test;
import org.oddjob.FailedToStopException;
import org.oddjob.Oddjob;
import org.oddjob.OurDirs;
import org.oddjob.tools.ConsoleCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class NoAnnotationsInJobTest {

    private static final Logger logger = LoggerFactory.getLogger(AnnotationsInJobTest.class);

    @Test
    public void noAnnotationsExample() throws FailedToStopException {

        OurDirs dirs = new OurDirs();

        File config = dirs.relative("examples/devguide/annotations2.xml");

        ConsoleCapture console = new ConsoleCapture();
        try (ConsoleCapture.Close close = console.captureConsole()) {

            Oddjob oddjob = new Oddjob();
            oddjob.setFile(config);

            oddjob.run();

            oddjob.stop();

            oddjob.softReset();

            oddjob.hardReset();

            oddjob.destroy();
        }

        console.dump(logger);

        String[] lines = console.getLines();

        assertThat(lines[0], is("Initialising we know we have children [Echo]"));
        assertThat(lines[1], startsWith("Configured - we now know that file is"));
        assertThat(lines[1], containsString("annotations2.xml"));
        assertThat(lines[2], is("Running"));
        assertThat(lines[3], is("I'm a child job"));
        assertThat(lines[4], is("Resetting"));
        assertThat(lines[5], startsWith("Destroying - we still have"));
        assertThat(lines[5], containsString("annotations2.xml"));
        assertThat(lines[5], containsString("Echo"));

    }
}