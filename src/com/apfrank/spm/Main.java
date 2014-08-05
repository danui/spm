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
            File srcRepoDir = getRepoDir(srcProjectDir);
            Path projectPath = Path.createFrom(srcRepoDir, srcProjectDir);
            tmpDir = FileTools.createTempDir();
            File repoDir = new File(tmpDir, "repo");
            Git git = GitTools.cloneRepository(srcRepoDir, repoDir,
                                               "master");

            // TODO: Implement Project
            //Project project = new Project(repoDir, git, projectPath);

            // TODO: Implement Presenter
            //Presenter presenter = new Presenter(project);
            //presenter.present(System.out);
        } finally {
            if (tmpDir != null) {
                FileTools.deleteRecursively(tmpDir);
            }
        }
    }

    private static File getProjectDir(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Usage: java -jar spm.jar <project dir>");
        }
        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            throw new Exception("Not a directory: " + args[0]);
        }
        return dir.getCanonicalFile();
    }

    private static File getRepoDir(File projectDir) throws Exception {
        File repoDir = GitTools.getRepoDir(projectDir);
        if (repoDir == null) {
            throw new Exception("Not in a Git repository: "
                                + projectDir.getPath());
        }
        return repoDir.getCanonicalFile();
    }

}