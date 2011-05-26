package org.oddjob.reference.ant;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class OurTask extends Task {

	private String stuff;
	
	@Override
	public void execute() throws BuildException {
		System.out.println(stuff);
	}
	
	public void setStuff(String stuff) {
		this.stuff = stuff;
	}
}
