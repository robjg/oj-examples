package org.oddjob.devguide;

import org.oddjob.arooa.deploy.annotations.ArooaAttribute;
import org.oddjob.arooa.deploy.annotations.ArooaComponent;
import org.oddjob.arooa.life.Configured;
import org.oddjob.arooa.life.Destroy;
import org.oddjob.arooa.life.Initialised;
import org.oddjob.framework.adapt.HardReset;
import org.oddjob.framework.adapt.Run;
import org.oddjob.framework.adapt.SoftReset;
import org.oddjob.framework.adapt.Stop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnnotationsInJob {

    @ArooaAttribute
    public File file;

    public final List<Object> children = new ArrayList<>();

    @Run
    public void go() {
        System.out.println("Running");
        for (Object child : children) {
            if (child instanceof Runnable) {
                ((Runnable) child).run();
            }
        }
    }

    @Stop
    public void halt() {
        System.out.println("Stopping");
    }

    @SoftReset
    @HardReset
    public void reset() {
        System.out.println("Resetting");
    }

    @Initialised
    public void init() {
        System.out.println("Initialising we know we have children " + children );
    }

    @Configured
    public void configure() {
        System.out.println("Configured - we now know that file is " + file);
    }

    @Destroy
    public void destroy() {
        System.out.println("Destroying - we still have " + file + " and " + children + " but not for long.");
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @ArooaComponent
    public void setChild(int index, Object child) {
        if (child == null) {
            this.children.remove(index);
        }
        else {
            this.children.add(index, child);
        }
    }

    @Override
    public String toString() {
        return "AnnotationsInJob";
    }
}
