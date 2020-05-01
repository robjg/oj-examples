package org.oddjob.devguide;

import org.oddjob.framework.extend.SimpleJob;

public class ExtendedJob extends SimpleJob {

	protected int execute() {
		System.out.println("Hello World!");
		return 0;
	}
}
