package com.apfrank.spm;

public class DataPoint {

    private Commit commit;
    private Path path;
    private int todoCount;
    private int doneCount;

    public DataPoint(Commit commit, Path path) {
        this.commit = commit;
        this.path = path;
        this.todoCount = 0;
        this.doneCount = 0;
    }

    public int getTodoCount() {
        return todoCount;
    }

    public int getDoneCount() {
        return doneCount;
    }

    public int getTotalCount() {
        return todoCount + doneCount;
    }

    public void incTodoCount() {
        todoCount += 1;
    }

    public void incDoneCount() {
        doneCount += 1;
    }
}
