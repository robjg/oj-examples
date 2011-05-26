package org.oddjob;

import java.io.File;

import org.oddjob.io.FilesType;
import org.oddjob.util.URLClassLoaderType;

public class Setup {

	final ClassLoader classLoader;
	
	final File oddballs;
	
	public Setup() {
		
		OurDirs binary = new OurDirs("dist.bin.dir");
		
		this.oddballs = binary.relative("oddballs");
		if (!oddballs.exists()) {
			throw new IllegalStateException("No Oddballs dir!");
		}
		
		URLClassLoaderType classLoader = new URLClassLoaderType();
		classLoader.setFiles(classPath(binary.base()));
		classLoader.setParent(getClass().getClassLoader());
	
		this.classLoader = classLoader.toValue();
	}
	
	File[] classPath(File base) {
		
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

	public File getOddballs() {
		return oddballs;
	}
}
