package com.apfrank.spm;

import java.io.File;
import java.util.LinkedList;
import java.util.Iterator;

public class Path implements Comparable<Path> {
    
    private LinkedList<String> nameList;
    
    /**
     * Create a Path from an iterable of names.
     */
    public static Path createFrom(Iterable<String> names) {
        Path path = new Path();
        Iterator<String> iter = names.iterator();
        while (iter.hasNext()) {
            path.addLast(iter.next());
        }
        return path;
    }
    
    public static Path createFrom(File base, File target) {
        Path path = new Path();
        File file = target;
        while (true) {
            if (file == null) {
                return null;
            } else if (file.equals(base)) {
                return path;
            } else {
                path.addFirst(file.getName());
                file = file.getParentFile();
            }
        }
    }
    
    /**
     * Create empty path.
     */
    public Path() {
        nameList = new LinkedList<String>();
    }

    public void addFirst(String name) {
        nameList.addFirst(name);
    }
    
    public void addLast(String name) {
        nameList.addLast(name);
    }
    
    public int compareTo(Path other) {
        File dotFile = new File(".");
        File a = this.getFile(dotFile);
        File b = other.getFile(dotFile);
        return a.compareTo(b);
    }
    
    public File getFile(File fromDir) {
        File file = fromDir;
        Iterator<String> iter = nameList.iterator();
        while (iter.hasNext()) {
            file = new File(file, iter.next());
        }
        if (file == fromDir) {
            file = new File(fromDir.getPath());
        }
        return file;
    }
    
    /**
     * Initialise empty path.
     */
    private void init() {
        nameList = new LinkedList<String>();
    }
    
}