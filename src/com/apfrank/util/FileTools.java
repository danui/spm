package com.apfrank.util;

import java.io.File;

import java.util.LinkedList;

public class FileTools {

    public static File createTempDir() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        String baseName = System.currentTimeMillis() + "-";
        for (int i = 0; i < 10000; ++i) {
            File tmpDir = new File(baseDir, baseName+i);
            if (tmpDir.mkdir()) {
                return tmpDir;
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

}
