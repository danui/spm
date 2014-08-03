package com.apfrank.spm;

import java.util.LinkedList;

public class SpmData {

    private SpmFile stories;
    private SpmFile backlog;
    private LinkedList<SpmFile> sprints;

    public SpmData() {
        stories = null;
        backlog = null;
        sprints = new LinkedList<SpmFile>();
    }

    public SpmData setStories(SpmFile spmFile) {
        stories = spmFile;
        return this;
    }

    public SpmData setBacklog(SpmFile spmFile) {
        backlog = spmFile;
        return this;
    }

    public SpmData addSprint(SpmFile spmFile) {
        sprints.add(spmFile);
        return this;
    }

    public SpmFile getStories() {
        return stories;
    }

    public SpmFile getBacklog() {
        return backlog;
    }

    public Iterable<SpmFile> getSprints() {
        return sprints;
    }

}
