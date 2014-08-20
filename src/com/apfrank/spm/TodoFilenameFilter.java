package com.apfrank.spm;

import java.io.FilenameFilter;
import java.io.File;

/**
 * File filter for .todo files
 */
public class TodoFilenameFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
        if (name.endsWith(".todo")) {
            return true;
        } else {
            return false;
        }
    }
}
