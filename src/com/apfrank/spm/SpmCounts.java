package com.apfrank.spm;

public class SpmCounts {

    private int todoCount;
    private int doneCount;

    public SpmCounts() {
        todoCount = 0;
        doneCount = 0;
    }

    public void setTodoCount(int ntodo) {
        todoCount = ntodo;
    }

    public void setDoneCount(int ndone) {
        doneCount = ndone;
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

}
