package org.oddjob.devguide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NoAnnotationsInJob {

    public File file;

    public final List<Object> children = new ArrayList<>();

    public void go() {
        System.out.println("Running");
        for (Object child : children) {
            if (child instanceof Runnable) {
                ((Runnable) child).run();
            }
        }
    }

    public void halt() {
        System.out.println("Stopping");
    }

    public void reset() {
        System.out.println("Resetting");
    }

    public void init() {
        System.out.println("Initialising we know we have children " + children );
    }

    public void configure() {
        System.out.println("Configured - we now know that file is " + file);
    }

    public void destroy() {
        System.out.println("Destroying - we still have " + file + " and " + children + " but not for long.");
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

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
