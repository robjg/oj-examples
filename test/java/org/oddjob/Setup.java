package org.oddjob;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.io.FilesType;
import org.oddjob.oddballs.OddballsDirDescriptorFactory;
import org.oddjob.tools.OurDirs;
import org.oddjob.util.URLClassLoaderType;

/**
 * Set's up the class loader and oddballs descriptor
 * factory to run off the newly built Oddjob distribution.
 * <p>
 * 
 * 
 * @author rob
 */
public class Setup {

	private static final Logger logger = LoggerFactory.getLogger(Setup.class);
	
	private final ClassLoader classLoader;
	
	private final ArooaDescriptorFactory oddballs;
	
	public Setup() throws IOException {
		
		if (System.getProperty("ant.file") == null) {
			logger.info("Not running from ant - assume running from eclipse and that classpath is setup.");
			classLoader = null;
			oddballs = null;
		}
		else {
			OurDirs binary = new OurDirs("dist.bin.dir");
			
			File oddballsDir = binary.relative("oddballs");
			if (!oddballsDir.exists()) {
				throw new IllegalStateException("No Oddballs dir!");
			}
			oddballs = new OddballsDirDescriptorFactory(oddballsDir);
		
			URLClassLoaderType classLoader = new URLClassLoaderType();
			classLoader.setFiles(classPath(binary.base()));
			classLoader.setParent(getClass().getClassLoader());
			classLoader.configured();
			
			this.classLoader = classLoader.toValue();
		}
	}
	
	File[] classPath(File base) throws IOException {
		
		FilesType lib = new FilesType();
		lib.setFiles(base + "/lib/*.jar");
		
		FilesType opt = new FilesType();
		
		opt.setFiles(base + "/opt/lib/*.jar");
		
		FilesType files = new FilesType();
		files.setList(0, lib.toFiles());
		files.setList(1, opt.toFiles());
		
		return files.toFiles();
	}
	
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public ArooaDescriptorFactory getOddballs() {
		return oddballs;
	}
}
