/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.launch;

import org.junit.*;
import org.junit.rules.TestName;
import org.oddjob.Main;
import org.oddjob.OddjobSrc;
import org.oddjob.OurDirs;
import org.oddjob.io.CopyJob;
import org.oddjob.util.URLClassLoaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.Assert.*;


/**
 * @author Rob Gordon.
 */
public class LauncherTest {

    private static final Logger logger = LoggerFactory.getLogger(LauncherTest.class);

    @Rule
    public TestName name = new TestName();
    String oddjobHome;

    public String getName() {
        return name.getMethodName();
    }

    @Before
    public void setUp() {
        logger.debug("------------------ " + getName() + " -----------------");

        oddjobHome = System.getProperty("oddjob.home");
    }

    @After
    public void tearDown() throws Exception {
        if (oddjobHome == null) {
            System.getProperties().remove("oddjob.home");
        } else {
            System.setProperty("oddjob.home", oddjobHome);
        }
    }

    @Test
    public void testGetClassLoader() throws IOException, URISyntaxException {

        File f = OddjobSrc.oddjobSrc().resolve( "src/main/java").toFile();

        ClassLoader cl = Launcher.getClassLoader(
                LauncherTest.class.getClassLoader(),
                new String[]{f.getPath()});

        assertNotNull("Classloader", cl);

        URL[] urls = ((URLClassLoader) cl).getURLs();

        HashSet<String> results = new HashSet<>();

        for (URL url : urls) {
            results.add(new File(url.toURI()).getCanonicalPath());
            System.out.println(url);
        }

        assertTrue(results.contains(
                f.getCanonicalPath()));

        assertTrue(System.getProperty(
                "java.class.path").contains(
                f.getCanonicalPath()));

        assertEquals(Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader());
    }


    @Test
    public void testInitOddjob() throws Exception {

        Path work = OurDirs.workPathDir(
                getClass().getSimpleName() + "-testInitOddjob",
                true);

        Path result = work.resolve("launcher.result");

        Path oddjobDir = OddjobSrc.oddjobSrc();

        System.setProperty("oddjob.home", new File(".").getCanonicalPath());

        String[] args = {
                "-nb",
                "-f",
                oddjobDir.resolve("test/conf/launcher-test.xml").toString(),
                work.toString()};

        Launcher test = new Launcher(Main.class.getClassLoader(),
                Launcher.ODDJOB_MAIN_CLASS, args);
        test.launch();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        CopyJob copy = new CopyJob();
        copy.setFrom(new File[]{result.toFile()});
        copy.setOutput(out);

        copy.run();

        assertEquals("Launcher Worked", out.toString().trim());

        assertEquals(new File(".").getCanonicalPath(),
                System.getProperty("oddjob.home"));

        assertEquals(Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader());
    }

    @Ignore("Fails on Java 11 with java.sql.Date not found. Probably a modules thing")
    @Test
    public void testLaunchAsJob() throws Exception {

        Path oddjobDir = OddjobSrc.oddjobSrc();

        Path workDir = OurDirs.workPathDir(
                getClass().getSimpleName() + "-testLaunchAsJob",
                true);

        Path result = workDir.resolve("launcher.result");

        System.setProperty("oddjob.home", "Will Be Overritten");

        File runJar = OddjobSrc.appJar().toFile();

        URLClassLoaderType classLoader = new URLClassLoaderType();
        classLoader.setFiles(new File[]{runJar});
        classLoader.setNoInherit(true);
        classLoader.configured();

        ClassLoader context = Thread.currentThread().getContextClassLoader();
        assertNotNull(context);

        try {
            Thread.currentThread().setContextClassLoader(null);

            Launcher boot = new Launcher(classLoader.toValue(),
                    Launcher.class.getName(),
                    new String[]{
                            "-f",
                            oddjobDir.resolve("test/conf/launcher-boot.xml").toString(),
                            workDir.toString()
                    });

            boot.launch();
        } finally {
            Thread.currentThread().setContextClassLoader(context);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        CopyJob copy = new CopyJob();
        copy.setFrom(new File[] { result.toFile() });
        copy.setOutput(out);

        copy.run();

        assertEquals("Launcher Worked", out.toString().trim());

        assertEquals(runJar.getParentFile().getCanonicalPath(),
                System.getProperty("oddjob.home"));

        assertEquals(Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader());
    }

}
