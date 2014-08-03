package com.apfrank.spm;

import java.util.Iterator;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.lib.Ref;

public class GitTools {

    /**
     * Checkout branch, GitLog on path, and for each commit in the log,
     * create a branch with name equal to the commit hash.
     */
    public void labelCommits(Git git, String branch, String path) throws Exception {
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

    public Iterator<RevCommit> getCommits(Git git, String branch, String path) throws Exception {
        LogCommand log = git.log();
        log.addPath(path);
        log.add(git.getRepository().resolve(branch));
        return log.call().iterator();
    }

    public Ref checkoutBranch(Git git, String branch) throws Exception {
        CheckoutCommand co = git.checkout();
        co.setName(branch);
        co.setStartPoint(branch);
        return co.call();
    }

}
