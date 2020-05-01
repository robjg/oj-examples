package org.oddjob.cmdln;

import org.junit.Test;
import org.oddjob.OddjobSrc;
import org.oddjob.tools.ConsoleCapture;
import org.oddjob.util.URLClassLoaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class URLClassLoaderTypeLaunchTest {

	private static final Logger logger = 
		LoggerFactory.getLogger(URLClassLoaderTypeLaunchTest.class);
	
    @Test
	public void testFromLaunchJar() throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, IOException {
		
		Path oddjobDir = OddjobSrc.oddjobSrc();
		
		File check = oddjobDir.resolve("test/classloader/AJob.class").toFile();
		
		if (!check.exists()) {
			throw new IllegalStateException("Run oddjob tests first");
		}

		File oddjobFile = oddjobDir.resolve( "test/classloader/classloader-test.xml").toFile();

		File runOddjob = OddjobSrc.appJar().toFile();
		
		URLClassLoaderType first = new URLClassLoaderType();
		first.setFiles(new File[] { runOddjob });
		first.setNoInherit(true);
		
		first.configured();
		
		ClassLoader loader = first.toValue();
		
		Class<?> launcher = loader.loadClass("org.oddjob.launch.Launcher");
		
		Method m = launcher.getMethod("main", String[].class);
		
		String[] args = new String[] { "-f", oddjobFile.getCanonicalPath() };
				
		ConsoleCapture console = new ConsoleCapture();
		try (ConsoleCapture.Close ignored = console.captureConsole()) {
			
			m.invoke(null, (Object) args);
		}
		
		console.dump(logger);
		
		String[] lines = console.getLines();
		
		assertEquals("Worked.", lines[1].trim());
		
	}	
}
