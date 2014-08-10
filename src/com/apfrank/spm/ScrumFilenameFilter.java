package com.apfrank.spm;

import java.io.FilenameFilter;
import java.io.File;

/**
 * File filter for scrum styled file names.
 */
public class ScrumFilenameFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        if (name.equals("stories.todo")) {
            return true;
        } else if (name.equals("backlog.todo")) {
            return true;
        } else if (name.startsWith("sprint-") &&
                   name.endsWith(".todo")) {
            return true;
        } else {
            return false;
        }
    }
}