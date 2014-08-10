package com.apfrank.spm;

public final class TaskState implements Comparable<TaskState> {

    public static final TaskState TODO;
    public static final TaskState WIP;
    public static final TaskState DONE;
    public static final TaskState CANCEL;
    public static final TaskState MAYBE;
    
    static {
        TODO = new TaskState("TODO"        , 0);
        WIP = new TaskState("WIP"          , 1);
        MAYBE = new TaskState("MAYBE"      , 2);
        DONE = new TaskState("DONE"        , 3);
        CANCEL = new TaskState("CANCEL"    , 4);
    }
    
    private String name;
    private Integer rank;
    
    private TaskState(String name, int rank) {
        this.name = name;
        this.rank = new Integer(rank);
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getRank() {
        return rank;
    }
    
    @Override
    public String toString() {
        return getName();
    }
    
    @Override
    public int compareTo(TaskState other) {
        return this.getRank().compareTo(other.getRank());
    }
}
