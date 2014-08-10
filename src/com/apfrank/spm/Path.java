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

    /**
     * Create a path from a 'base' to a 'target'.
     *
     * @return Path with names from 'base' to 'target'.
     *         Returns null if 'base' is not an ancestor of 'target'.
     */
    public static Path createFrom(File base, File target) {
        try {
            Path p = new Path(base, target);
            return p;
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * Create empty path.
     */
    public Path() {
        init();
    }
    
    public Path(Path other, String name) {
        init();
        Iterator<String> iter = other.getNameIterator();
        while (iter.hasNext()) {
            this.addLast(iter.next());
        }
        this.addLast(name);
    }
    
    public Path(File base, File target) {
        init();
        File file = target;
        while (true) {
            if (file == null) {
                throw new RuntimeException(
                    "base is not an ancestor of target");
            } else if (file.equals(base)) {
                break;
            } else {
                addFirst(file.getName());
                file = file.getParentFile();
            }
        }
    }

    public String getLastName() {
        return nameList.getLast();
    }
    
    public Iterator<String> getNameIterator() {
        return nameList.iterator();
    }

    public void addFirst(String name) {
        nameList.addFirst(name);
    }

    public void addLast(String name) {
        nameList.addLast(name);
    }

    @Override
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

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        Iterator<String> iter = nameList.iterator();
        while (iter.hasNext()) {
            if (sb.length() > 0) {
                sb.append(File.separatorChar);
            }
            sb.append(iter.next());
        }
        return sb.toString();
    }

    /**
     * Initialise empty path.
     */
    private void init() {
        nameList = new LinkedList<String>();
    }

}