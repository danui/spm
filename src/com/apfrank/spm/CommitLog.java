package com.apfrank.spm;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Date;
import java.util.Map;

/**
 * An object for storing Commits.
 */
public class CommitLog {

    private HashMap<String,Commit> commitsByHash;
    private TreeMap<Date,Commit> commitsByDate;

    public CommitLog() {
        commitsByHash = new HashMap<String,Commit>();
        commitsByDate = new TreeMap<Date,Commit>();
    }

    /**
     * Add Commit to database.
     */
    public void addCommit(Commit commit) {
        commitsByHash.put(commit.getHash(), commit);
        commitsByDate.put(commit.getDate(), commit);
    }

    /**
     * Get Commit with 'hash'.
     *
     * @return Commit, or null if there is none matching 'hash'.
     */
    public Commit getCommit(String hash) {
        return commitsByHash.get(hash);
    }

    /**
     * Get newest Commit before 'date'.
     *
     * @return Commit, or null if there is no commit before 'date'.
     */
    public Commit getCommitBefore(Date date) {
        Map.Entry<Date,Commit> entry = commitsByDate.lowerEntry(date);
        if (entry == null)
            return null;
        return entry.getValue();
    }

}