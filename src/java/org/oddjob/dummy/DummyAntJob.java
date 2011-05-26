/*
 * Copyright (c) 2005, Rob Gordon.
 */
package org.oddjob.dummy;

import org.apache.tools.ant.Project;
import org.oddjob.ant.AntJob;

/**
 *
 * @author Rob Gordon.
 */
public class DummyAntJob extends AntJob {
	private static final long serialVersionUID = 2009042400L;
	
    public DummyAntJob() {
        Project project = new Project();
        project.init();
        project.addTaskDefinition("ftp", DummyFtpTask.class);
        project.addTaskDefinition("mail", DummyEmailTask.class);
        setProject(project);
	}
}
