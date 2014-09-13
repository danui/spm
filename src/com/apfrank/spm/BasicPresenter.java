package com.apfrank.spm;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Date;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;


public class BasicPresenter implements Presenter {

    private Project project;
    private PrintStream out;
    
    public BasicPresenter(Project project) {
        this.project = project;
        this.out = System.out;
    }
    
    public void present() throws Exception {
        out.println("Project Name: " + project.getName());
        out.println("Project File Count: " + project.getFileCount());
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            presentTodoFile(todoFile);
        }
    }
    
    private void presentTodoFile(TodoFile todoFile)
        throws Exception
    {
        out.println("TodoFile: " + todoFile.getName());
        out.format("  Date range: %s -> %s%n",
                   todoFile.getFirstDate(),
                   todoFile.getLastDate());
        presentTodoFileCounts(todoFile);
        presentTodoFileContents(todoFile);
    }
    
    private void presentTodoFileCounts(TodoFile todoFile) {
        Iterator<Date> iter = DateGenerator.createNatural(
            todoFile.getFirstDate(), todoFile.getLastDate());
        while (iter.hasNext()) {
            Date d = iter.next();
            int todo = todoFile.getTodoCount(d);
            int total = todoFile.getTotalCount(d);
            double percent = 100.0 * todo / total;
            out.format("  %s %8d %8d %6.2f%n",
                       d, todo, total, percent);
        }
    }
    
    private void presentTodoFileContents(TodoFile todoFile)
        throws Exception
    {
        File file = todoFile.getFile();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int i = 0;
        while (null != (line = reader.readLine())) {
            i += 1;
            out.format("%7d %s%n", i, line);
        }
    }
}
