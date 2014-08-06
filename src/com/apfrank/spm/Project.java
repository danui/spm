package com.apfrank.spm;

import org.eclipse.jgit.api.Git;

import java.util.TreeSet;

import java.io.File;
import java.io.FilenameFilter;

public class Project {

    private Git git;
    private File workingDir;
    private Path projectPath;
    private File projectDir;

    private TreeSet<Path> todoPaths;

    public Project(File srcDir, File wrkDir, String branch)
        throws Exception {

        File srcRepoDir = GitTools.getRepoDir(srcDir);
        if (srcRepoDir == null) {
            throw new UsageException("Not in a Git repository: "
                                    + srcDir.getPath());
        }

        workingDir = wrkDir;
        projectPath = Path.createFrom(srcRepoDir, srcDir);
        git = GitTools.cloneRepository(srcRepoDir, wrkDir, branch);
        projectDir = projectPath.getFile(workingDir);

        todoPaths = new TreeSet<Path>();

        scanForPaths();
        // TODO: buildCommitLog();
        // TODO: attachDataPoints();
        // TODO: counting();
    }

    private void scanForPaths() {
        File[] files = projectDir.listFiles(new FilenameFilter() {
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
        });
        for (int i = 0; i < files.length; ++i) {
            File f = files[i];
            if (f.isFile()) {
                Path p = new Path(projectPath, f.getName());
                todoPaths.add(p);
                System.out.println(p);
            }
        }
    }
}
