package org.oddjob.cmdln;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.tools.OurDirs;
import org.oddjob.util.URLClassLoaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class URLClassLoaderTypeLaunchTest {

	private static final Logger logger = 
		LoggerFactory.getLogger(URLClassLoaderTypeLaunchTest.class);
	
    @Test
	public void testFromLaunchJar() throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		
		File oddjobDir = new OurDirs().relative("../oddjob");
		
		File check = new File(oddjobDir, "test/classloader/AJob.class");
		
		if (!check.exists()) {
			throw new IllegalStateException("Run oddjob tests first");
		}
		
		
		File oddjobFile = new File(oddjobDir, "test/classloader/classloader-test.xml");

		File runOddjob = new File(oddjobDir, "run-oddjob.jar");
		
		URLClassLoaderType first = new URLClassLoaderType();
		first.setFiles(new File[] { runOddjob });
		first.setNoInherit(true);
		
		first.configured();
		
		ClassLoader loader = first.toValue();
		
		Class<?> launcher = loader.loadClass("org.oddjob.launch.Launcher");
		
		Method m = launcher.getMethod("main", String[].class);
		
		String[] args = new String[] { "-f", oddjobFile.getCanonicalPath() };
				
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close close = console.captureConsole()) {
			
			m.invoke(null, (Object) args);
		}
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals("Worked.", lines[1].trim());
		
	}	
}
