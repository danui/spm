package com.apfrank.spm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

public class Html5Presenter implements Presenter {

    private Project project;
    private PrintStream out;
    
    public Html5Presenter(Project project) {
        this.project = project;
        this.out = System.out;
    }

    public void present() throws Exception {
        presentTitle();
        presentNav();
        presentTodoFileContentSection();
        presentTodoFileDataSection();
    }
    
    private void presentTitle() throws Exception {
        presentResource("html5/titleBegin.html");
        out.println(project.getName());
        presentResource("html5/titleEnd.html");
    }
    
    private void presentNav() throws Exception {
        presentResource("html5/navOptionsBegin.html");
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            presentNavOption(todoFile.getName());
        }
        presentResource("html5/navOptionsEnd.html");
    }
    
    private void presentNavOption(String name) throws Exception {
        out.format("<li><a href=\"%s\">%s</a></li>%n",
                   name, name);
    }

    private void presentTodoFileContentSection() throws Exception {
        presentResource("html5/todoFileContentSectionBegin.html");
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            presentTodoFileContent(todoFile);
        }
        presentResource("html5/todoFileContentSectionEnd.html");
    }

    private void presentTodoFileContent(TodoFile todoFile)
        throws Exception
    {
        String name = todoFile.getName();
        String chartName = name + ".chart";
        out.format(
            "<h1 id=\"%s\" class=\"padtop page-header\">%s</h1>%n" +
            "<div class=\"chartrow\">%n"+
            "<div id=\"%s\" class=\"chartarea\"></div>%n"+
            "</div><div class=\"textrow\"><pre>%n",
            name, name, chartName);
        presentFile(todoFile.getFile());
        out.format("</pre></div>%n");
    }
    
    private void presentTodoFileDataSection() throws Exception {
        presentResource("html5/todoFileDataSectionBegin.html");
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            presentTodoFileData(todoFile);
        }
        presentResource("html5/todoFileDataSectionEnd.html");
    }
    
    private void presentTodoFileData(TodoFile todoFile)
        throws Exception
    {
        String name = todoFile.getName();
        String chartName = name + ".chart";
        out.format("(function () {var plot = $.jqplot('%s', [[%n",
                   chartName);
        Iterator<DataPoint> iter = todoFile.getDataPointIterator();
        boolean first = true;
        while (iter.hasNext()) {
            DataPoint point = iter.next();
            if (!first) {
                out.format(",");
            }
            out.format("[%d,%d]%n",
                       point.getDate().getTime(),
                       point.getCount("TODO"));
        }
        out.format("]]);$(window).resize(function () { " +
                   "plot.replot({resetAxes:true});});" +
                   "}());%n");
    }
    
    private void presentFile(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        presentBufferedReader(reader);
    }
    
    private void presentResource(String path) throws Exception {
        BufferedReader reader = loadFromJar(path);
        if (reader != null) {
            presentBufferedReader(loadFromJar(path));
        } else {
            out.format("<!-- resource not found: %s -->%n", path);
        }
    }
    
    private void presentBufferedReader(BufferedReader reader)
        throws Exception
    {
        if (reader != null) {
            String line;
            while (null != (line = reader.readLine())) {
                out.println(line);
            }
        }
    }
    
    private BufferedReader loadFromJar(String path) {
        InputStream inputStream = this.getClass()
            .getClassLoader()
            .getResourceAsStream(path);
        if (inputStream == null) {
            return null;
        }
        return new BufferedReader(new InputStreamReader(inputStream));
    }
}
