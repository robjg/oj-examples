package org.oddjob;

import org.oddjob.arooa.deploy.ArooaDescriptorFactory;
import org.oddjob.io.FilesType;
import org.oddjob.oddballs.OddballsDescriptorFactory;
import org.oddjob.oddballs.OddballsDirDescriptorFactory;
import org.oddjob.util.URLClassLoaderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
			logger.info("Not running from ant - building Oddballs from modules");

			List<String> modules = Arrays.asList("oj-ant", "oj-mail", "oj-net");

			File[] oddballDirs = modules.stream()
					.map(m -> OurDirs.basePath()
							.resolve("..")
							.resolve(m)
							.resolve(OurDirs.buildType().getBuildDir())
							.resolve("oddball")
							.toFile())
					.toArray(File[]::new);

			OddballsDescriptorFactory odf = new OddballsDescriptorFactory();
			odf.setFiles(oddballDirs);

			classLoader = null;
			oddballs = odf;
		}
		else {
			String binaryDist = System.getProperty("dist.bin.dir");
			if (binaryDist == null) {
				throw new IllegalStateException("No idea how this is running");
			}

			File binary = new File(binaryDist);
			
			File oddballsDir = new File(binary, "oddballs");
			if (!oddballsDir.exists()) {
				throw new IllegalStateException("No Oddballs dir!");
			}
			oddballs = new OddballsDirDescriptorFactory(oddballsDir);
		
			URLClassLoaderType classLoader = new URLClassLoaderType();
			classLoader.setFiles(classPath(binary));
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
