package org.oddjob.devguide;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.oddjob.util.ClassLoaderDiagnostics;

/**
 *
 <tasks xmlns:my="my:org.oddjob.devguide">
   <taskdef uri="my:org.oddjob.devguide"
      resource="org/oddjob/devguide/antlib.xml"
      classpath="../classes"/>
    <my:hello/>
 </tasks>
 * 
 * @author rob
 *
 */
public class MyAntTask extends Task {

	@Override
	public void execute() throws BuildException {
		super.execute();
		
		System.out.println("Hello From My Ant Task!");
		
		ClassLoaderDiagnostics.logClassLoaderStack(
				getClass().getClassLoader());
	}
}
