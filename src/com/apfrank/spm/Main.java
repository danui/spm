package com.apfrank.spm;

import java.io.File;
import org.eclipse.jgit.api.Git;

/**
 * Main program of spm.jar.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        File tmpDir = null;
        try {
            File srcProjectDir = getProjectDir(args);

            File argDir = getProjectDir(args);
            tmpDir = FileTools.createTempDir();
            File wrkDir = new File(tmpDir, "work");
            Project project = new Project(argDir, wrkDir, "master");

            // TODO: Implement Presenter
            //Presenter presenter = new Presenter(project);
            //presenter.present(System.out);
        } catch (UsageException e) {
            System.out.println(e.getMessage());
        } finally {
            if (tmpDir != null) {
                FileTools.deleteRecursively(tmpDir);
            }
        }
    }

    private static File getProjectDir(String[] args) throws Exception {
        if (args.length != 1) {
            throw new UsageException("Usage: java -jar spm.jar <project dir>");
        }
        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            throw new UsageException("Not a directory: " + args[0]);
        }
        return dir.getCanonicalFile();
    }
}