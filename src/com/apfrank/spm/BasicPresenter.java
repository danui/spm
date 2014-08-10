package com.apfrank.spm;

import java.io.PrintStream;
import java.util.Iterator;

public class BasicPresenter implements Presenter {

    Project project;
    PrintStream out;
    
    public BasicPresenter(Project project) {
        this.project = project;
        this.out = System.out;
    }
    
    public void present() {
        out.println("Project Name: " + project.getName());
        out.println("Project File Count: " + project.getFileCount());
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            out.println("TodoFile: " + iter.next().getName());
        }
    }
}
