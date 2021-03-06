package com.apfrank.spm;

import java.io.File;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Date;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.lib.PersonIdent;


public class GitTools {

    private static final String HEAD = org.eclipse.jgit.lib.Constants.HEAD;
    
    /**
     * Get the top-level directory of the repository within 'dir'
     * resides.
     *
     * @return Repository Directory, null if dir is not in a Git
     * Repository.
     */
    public static File getRepositoryDir(File dir) {
        FileRepositoryBuilder b = new FileRepositoryBuilder();
        b.findGitDir(dir);
        File dotGit = b.getGitDir();
        if (dotGit == null) {
            return null;
        } else {
            return dotGit.getParentFile();
        }
    }
    
    /**
     * Get the top-level directory of a 'git' repository.
     */
    public static File getRepositoryDir(Git git) {
        File dotGit = git.getRepository().getDirectory();
        if (dotGit == null) {
            return null;
        } else {
            return dotGit.getParentFile();
        }
    }

    /**
     * Checkout branch, GitLog on path, and for each commit in the log,
     * create a branch with name equal to the commit hash.
     */
    public static void labelCommits(Git git, String branch, String path) throws Exception {
        checkoutBranch(git, branch);
        Iterator<RevCommit> iter = getCommits(git, branch, path);
        while (iter.hasNext()) {
            RevCommit commit = iter.next();
            CheckoutCommand co = git.checkout();
            co.setName(commit.getName());
            co.setStartPoint(commit);
            co.setCreateBranch(true);
            co.call();
        }
    }

    public static Iterator<RevCommit> getCommits(Git git, String branch, String path) throws Exception {
        LogCommand log = git.log();
        log.addPath(path);
        log.add(git.getRepository().resolve(branch));
        return log.call().iterator();
    }

    public static Ref checkoutBranch(Git git, String branch) throws Exception {
        CheckoutCommand co = git.checkout();
        co.setName(branch);
        co.setStartPoint(branch);
        return co.call();
    }

    /**
     * Clone repository at srcDir to destDir.
     */
    public static Git cloneRepository(File srcDir, File destDir,
                                      String branch) throws Exception {
        ArrayList<String> branches = new ArrayList<String>();
        branches.add(branch);
        CloneCommand clone = new CloneCommand();
        clone.setRemote("origin");
        clone.setBranch(branch);
        clone.setBranchesToClone(branches);
        clone.setURI(srcDir.getPath());
        clone.setDirectory(destDir);
        return clone.call();
    }
    
    public static String readCommitHash(RevCommit revCommit) {
        return revCommit.getName();
    }
    
    public static Date readCommitDate(RevCommit revCommit) {
        PersonIdent auth = revCommit.getAuthorIdent();
        return auth.getWhen();
    }
}
