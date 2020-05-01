package org.oddjob.devguide;

import org.junit.Before;

import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.junit.Assert;

import org.oddjob.Oddjob;
import org.oddjob.state.ParentState;
import org.oddjob.OurDirs;

public class ModularityAndOddballsTest extends Assert {

    File dir;

    File base;

    @Before
    public void setUp() throws Exception {

        base = new OurDirs().base();
        dir = new File(base, "examples/devguide");

        assertTrue(dir.exists());
    }

    @Test
    public void testHelloPersonMain() {

        Oddjob oj = new Oddjob();
        oj.setFile(new File(dir, "helloperson-main.xml"));
        oj.run();

        assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

        oj.destroy();
    }

    @Test
    public void testHelloPersonMain2() {

        Properties props = new Properties();
		props.setProperty("my-app.dir", antOddball());

        Oddjob oj = new Oddjob();
        oj.setFile(new File(dir, "myapp-main.xml"));
        oj.setProperties(props);
        oj.run();

        assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

        oj.destroy();
    }

    @Test
    public void testExtendingOddballClassLoader() {

        Properties props = new Properties();
        props.setProperty("my-app.dir", antOddball());

        Oddjob oj = new Oddjob();
        oj.setFile(new File(dir, "myapp2-main.xml"));
        oj.setProperties(props);
        oj.run();

        assertEquals(ParentState.COMPLETE, oj.lastStateEvent().getState());

        oj.destroy();
    }

    String antOddball() {

        Path antOddball =  OurDirs.basePath()
				.resolve("../oj-ant")
                .resolve(OurDirs.buildType().getBuildDir())
                .resolve("oddball");

        if (!Files.exists(antOddball)) {
			throw new IllegalStateException("Can't find " + antOddball);
		}

        return antOddball.toString();
    }


}
