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
            File projectDir = getProjectDir(args);
            File repositoryDir = getRepositoryDir(projectDir);
            Path projectPath = Path.createFrom(repositoryDir, projectDir);

            // Declaration of tmpDir resides outside of 'try' construct
            // because we want to delete it in 'finally'.
            tmpDir = FileTools.createTempDir();

            File cloneDir = new File(tmpDir, "clone");
            String branch = "master";
            Git git = GitTools.cloneRepository(repositoryDir, cloneDir,
                                               branch);

            Project project = new Project(
                git, branch, projectPath,
                new ScrumFilenameFilter());
            
            
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

    /**
     * Get project directory File object.
     *
     * A project directory may be a subdirectory of a repository.
     */
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
    
    /**
     * Get the repository directory File that contains a project
     * directory File.
     *
     * @return File of repository directory.
     */
    private static File getRepositoryDir(File projectDir) throws Exception {
        File dir = GitTools.getRepositoryDir(projectDir);
        if (dir == null) {
            throw new UsageException(
                "Project directory is not in a Git repository: "
                + projectDir.getPath());
        }
        return dir.getCanonicalFile();
    }
}