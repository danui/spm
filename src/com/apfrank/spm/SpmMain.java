package com.apfrank.spm;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FilenameFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.apfrank.util.FileTools;

public class SpmMain {

    public static void main(String[] args) throws Exception {
        File tmpDir = null;
        try {

            // Must specify a git repository.
            if (args.length != 1) {
                System.out.println("Must specify path to a Git Repository.");
                System.exit(1);
            }

            File repoDir = new File(args[0]);
            if (!repoDir.isDirectory()) {
                System.out.println("Not a directory: " + repoDir.getCanonicalPath());
                System.exit(1);
            }

            FileRepositoryBuilder repoBuilder = new FileRepositoryBuilder();
            repoBuilder.findGitDir(repoDir);
            if (null == repoBuilder.getGitDir()) {
                System.out.println("Not a Git Repository: " + repoDir.getCanonicalPath());
                System.exit(1);
            }

            Repository repo = repoBuilder.build();

            tmpDir = FileTools.createTempDir();
            File workingDir = new File(tmpDir, "working");

            // TODO: branchName should be an option.
            String branchName = "master";
            ArrayList<String> branchesToClone = new ArrayList<String>();
            branchesToClone.add(branchName);

            CloneCommand cloneCommand = new CloneCommand();
            cloneCommand.setRemote("origin");
            cloneCommand.setBranch(branchName);
            cloneCommand.setBranchesToClone(branchesToClone);
            cloneCommand.setURI(repo.getDirectory().getCanonicalPath());
            cloneCommand.setDirectory(workingDir);

            Git git = cloneCommand.call();

            // TODO: projectPath should be an option.
            String projectPath = "scrum";

            File projectDir = new File(workingDir, projectPath);

            SpmData data = new SpmData();

            data.setStories(curateFile(
                    new File(projectDir, "stories.todo"),
                    git,
                    branchName));
                    
            File storiesFile = new File(projectDir, "stories.todo");
            File backlogFile = new File(projectDir, "backlog.todo");
            File[] sprintFiles = projectDir.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.startsWith("sprint") && name.endsWith(".todo");
                    }
                });
            System.out.println(storiesFile);
            System.out.println(backlogFile);
            for (int i = 0; i < sprintFiles.length; ++i) {
                System.out.println(sprintFiles[i].getCanonicalPath());
            }
        } finally {
            if (tmpDir != null) {
                FileTools.deleteRecursively(tmpDir);
            }
        }
    }

    private static SpmFile curateFile(File file, Git git, String branch) throws Exception {
        CheckoutCommand checkout = git.checkout();
        checkout.setStartPoint(branch);
        checkout.call();

        if (!file.isFile()) {
            throw new Exception("File not found: " + file.getCanonicalPath());
        }
        System.out.println("Found file: " + file.getCanonicalPath());

        LogCommand log = git.log();
        log.addPath(file.getCanonicalPath());

        return null;
    }

    // TODO: delete below...


    private static class Commit {
        public String hash;
        public long epochSeconds;
        public double getDays() {
            return (double)epochSeconds / 60 / 60 / 24;
        }
        public String getShortHash() {
            return hash.substring(0,7);
        }
    }

    private static class Counts {
        public long openCount;
        public long closeCount;
        public long cancelCount;
        public long deferCount;

        public void add(Counts other) {
            openCount += other.openCount;
            closeCount += other.closeCount;
            cancelCount += other.cancelCount;
            deferCount += other.deferCount;
        }

        public long getTotal() {
            return openCount + closeCount + cancelCount + deferCount;
        }
    }



    public SpmMain(String[] args) throws Exception {

        // Change args into an array of files.
        File[] files = getFiles(args);

        // Change files into paths.
        String[] paths = getPaths(files);

        execCommand("git stash save");
        execCommand("git checkout master");

        // Get list of GIT commits
        LinkedList<Commit> commits = getCommits(paths);

        processCommits(commits, paths);

        execCommand("git checkout master");
        execCommand("git stash pop");
    }

    private void execCommand(String cmd) throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process child = rt.exec(cmd);
        child.waitFor();
    }

    private File[] getFiles(String[] args) throws Exception {
        File[] files = new File[args.length];
        for (int i=0; i<args.length; ++i) {
            files[i] = new File(args[i]);
            if (!files[i].isFile())
                throw new Exception("Not a file: " + args[i]);
        }
        return files;
    }

    private String[] getPaths(File[] files) {
        String[] paths = new String[files.length];
        for (int i = 0; i < files.length; ++i) {
            paths[i] = files[i].getAbsolutePath();
        }
        return paths;
    }

    private LinkedList<Commit> getCommits(String[] paths) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = new String[4 + paths.length];
        cmd[0] = "git";
        cmd[1] = "log";
        cmd[2] = "--reverse";
        cmd[3] = "--format=format: %H %at";
        for (int i = 0; i < paths.length; ++i) {
            cmd[4+i] = paths[i];
        }
        Process child = rt.exec(cmd);
        child.waitFor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(child.getInputStream()));
        Pattern pattern = Pattern.compile("([0-9a-f]{1,}) ([0-9]{1,})");
        String line;
        LinkedList<Commit> commits = new LinkedList<Commit>();
        while (null != (line = reader.readLine())) {
            Matcher matcher = pattern.matcher(line.trim());
            if (!matcher.matches()) {
                throw new Exception("Pattern match failed for line: " + line);
            }
            Commit commit = new Commit();
            commit.hash = matcher.group(1);
            commit.epochSeconds = Long.parseLong(matcher.group(2));
            commits.add(commit);
        }
        return commits;
    }

    private void processCommits(LinkedList<Commit> commits, String[] paths) throws Exception {
        Commit firstCommit = null;
        System.out.format("%7s %12s %8s %8s %8s %8s %8s%n",
            "#commit",
            "days",
            "total",
            "open",
            "close",
            "cancel",
            "defer"
        );
        Iterator<Commit> iter = commits.iterator();
        while (iter.hasNext()) {
            Commit commit = iter.next();
            Counts counts = getCommitCounts(commit, paths);
            double days = 0.0;
            if (firstCommit == null) {
                firstCommit = commit;
            } else {
                days = commit.getDays() - firstCommit.getDays();
            }
            System.out.format("%7s %12.6f %8d %8d %8d %8d %8d%n",
                commit.getShortHash(),
                days,
                counts.getTotal(),
                counts.openCount,
                counts.closeCount,
                counts.cancelCount,
                counts.deferCount
            );
        }
    }

    private Counts getCommitCounts(Commit commit, String[] paths) throws Exception {
        Runtime rt = Runtime.getRuntime();
        String[] cmd = new String[3];
        cmd[0] = "git";
        cmd[1] = "checkout";
        cmd[2] = commit.hash;
        Process child = rt.exec(cmd);
        child.waitFor();

        Counts counts = new Counts();
        for (int i=0; i < paths.length; ++i) {
            File f = new File(paths[i]);
            if (f.exists()) {
                Counts c = processFile(f);
                counts.add(c);
            }
        }
        return counts;
    }

    private Counts processFile(File file) throws Exception {
        Pattern openPattern = Pattern.compile(".*\\[ \\].*");
        Pattern closePattern = Pattern.compile(".*\\[X\\].*");
        Pattern cancelPattern = Pattern.compile(".*\\[C\\].*");
        Pattern deferPattern = Pattern.compile(".*\\[[?]\\].*");

        Counts counts = new Counts();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while (null != (line = reader.readLine())) {
            counts.openCount += countLine(line, openPattern);
            counts.closeCount += countLine(line, closePattern);
            counts.cancelCount += countLine(line, cancelPattern);
            counts.deferCount += countLine(line, deferPattern);
        }
        return counts;
    }

    private long countLine(String line, Pattern pattern) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches())
            return 1;
        return 0;
    }
}
