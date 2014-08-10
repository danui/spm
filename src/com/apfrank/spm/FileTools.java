package com.apfrank.spm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;

public class FileTools {

    public static File createTempDir() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";
        for (int i = 0; i < 10000; ++i) {
            File tmpDir = new File(baseDir, baseName+i);
            if (tmpDir.mkdir()) {
                return tmpDir.getCanonicalFile();
            }
        }
        throw new Exception("Could not make temporary directory.");
    }

    public static void deleteRecursively(File file) {
        if (file == null) {
            // Nothing to do.
        } else if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] children = file.listFiles();
            for (int i = 0; i < children.length; ++i) {
                deleteRecursively(children[i]);
            }
            file.delete();
        } else {
            // Do not know what to do!
        }
    }

    public static LinkedList<String> getPathNames(File baseDir, File descendent) {
        LinkedList<String> list = new LinkedList<String>();
        File i = descendent;
        while (true) {
            if (i.equals(baseDir)) {
                return list;
            } else {
                list.addFirst(i.getName());
                i = i.getParentFile();
            }
        }
    }

    public static SortedSet<String> findFilenames(
        File dir, FilenameFilter filter)
    {
        TreeSet<String> tree = new TreeSet<String>();
        File[] files = dir.listFiles(filter);
        for (int i = 0; i < files.length; ++i) {
            tree.add(files[i].getName());
        }
        return tree;
    }
    
}
