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
        out.println("<!DOCTYPE html><html lang=\"en\">");
        presentHead();
        presentBody();
        out.println("</html>");
    }
    
    private void presentHead() throws Exception {
        out.println("<head>");
        presentResource("html5/headMetadata.html");
        out.format("<title>%s</title>%n", project.getName());
        presentCss("html5/bootstrap.min.css");
        presentCss("html5/spm.css");
        out.println("</head>");
    }
    
    public void presentBody() throws Exception {
        out.println("<body>");
        out.println("<div class='container'>");
        presentNav();
        presentTodoFileContentSection();
        out.println("</div>");
        presentJavaScript("html5/jquery.min.js");
        presentJavaScript("html5/jquery.jqplot.min.js");
        presentJavaScript("html5/bootstrap.min.js");
        presentTodoFileDataSection();
        out.println("</body>");
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
        out.format("<li><a href=\"#%s\">%s</a></li>%n",
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
        String chartName = "chart" + todoFile.hashCode();
        out.format(
            "<h1 id=\"%s\" class=\"padtop page-header\">%s</h1>%n" +
            "<div class=\"chartrow\">%n"+
            "<div id=\"%s\" class=\"chartarea\"></div>%n"+
            "</div><div class=\"textrow\"><pre>%n",
            name, name, chartName);
        presentFile(todoFile.getFile());
        out.format("</pre></div>%n");
    }
    
    private void presentCss(String path) throws Exception {
        out.println("<style>");
        presentResource(path);
        out.println("</style>");
    }

    private void presentJavaScript(String path) throws Exception {
        out.println("<script type='text/javascript'>");
        out.println("(function () {");
        presentResource(path);
        out.println("}());");
        out.println("</script>");
    }
    
    private void presentTodoFileDataSection() throws Exception {
        out.println("<script type='text/javascript'>");
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            presentTodoFileData(todoFile);
        }
        out.println("</script>");
    }
    
    private void presentTodoFileData(TodoFile todoFile)
        throws Exception
    {
        String name = todoFile.getName();
        String chartName = "chart" + todoFile.hashCode();
        long baseTime = project.getBaseTime();
        out.format("$(document).ready(function () {");
        
        out.format("var plot = $.jqplot(\"%s\", [[%n",
                   chartName);
        Iterator<DataPoint> iter = todoFile.getDataPointIterator();
        boolean first = true;
        while (iter.hasNext()) {
            DataPoint point = iter.next();
            long time = point.getDate().getTime() - baseTime;
            double day = 1.0 * time / 1000.0 / 60.0 / 60.0 / 24.0;
            if (first) {
                first = false;
            } else {
                out.format(",");
            }
            out.format("[%.2f,%d]%n",
                       day,
                       point.getCount("TODO"));
        }
        out.format("]]);");
        
        out.format("$(window).resize(function () {%n"+
                   "    plot.replot({resetAxes:true});%n"+
                   "});%n");
        
        out.format("});%n");
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
            throw new Exception(String.format(
                "<!-- resource not found: %s -->%n", path));
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
