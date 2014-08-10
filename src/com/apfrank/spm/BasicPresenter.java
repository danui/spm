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
            TodoFile todoFile = iter.next();
            out.println("TodoFile: " + todoFile.getName());
            out.format("  Date range: %s -> %s%n",
                       todoFile.getFirstDate(),
                       todoFile.getLastDate());
            Iterator<DataPoint> dpIter = todoFile.getDataPointIterator();
            while (dpIter.hasNext()) {
                DataPoint dataPoint = dpIter.next();
                int todo = dataPoint.getCount("TODO");
                int total = dataPoint.getTotalCount();
                double percent = 100.0 * todo / total;
                out.format("  %s %8d %8d %6.2f%n",
                           dataPoint.getDate(),
                           todo, total, percent);
            }
        }
    }
}
