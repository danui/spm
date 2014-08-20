package com.apfrank.spm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Date;

import com.apfrank.json.JsonValue;
import com.apfrank.json.JsonObject;
import com.apfrank.json.JsonArray;
import com.apfrank.json.JsonString;
import com.apfrank.json.JsonNumber;

public class Html5Presenter implements Presenter {

    private Project project;
    private PrintStream out;
    
    public Html5Presenter(Project project) {
        this.project = project;
        this.out = System.out;
    }

    public void present() throws Exception {
        presentResource("html5/top.html");
        presentCss("html5/bootstrap.min.css");
        presentCss("html5/spm.css");
        presentResource("html5/middle.html");
        presentJavaScript("html5/jquery.min.js");
        presentJavaScript("html5/jquery.jqplot.min.js");
        presentJavaScript("html5/bootstrap.min.js");
        presentProject();
        presentJavaScript("html5/spm.js");
        presentResource("html5/bottom.html");
    }
    
    private String getTextId(TodoFile todoFile) throws Exception {
        return "text" + todoFile.getId();
    }
    
    private String getChartId(TodoFile todoFile) throws Exception {
        return "chart" + todoFile.getId();
    }
    
    private String getAnchorId(TodoFile todoFile) throws Exception {
        return "anchor" + todoFile.getId();
    }
    
    private int getFinalCount(TodoFile todoFile) throws Exception {
        return todoFile.getLastDataPoint().getCount("TODO");
    }
    
    private double getDays(Date date) {
        return date.getTime() / 1000.0 / 60.0 / 60.0 / 24.0;
    }
    
    private double getDuration(TodoFile todoFile) throws Exception {
        Date first = todoFile.getFirstDate();
        int finalCount = getFinalCount(todoFile);
        Date last;
        if (finalCount > 0) {
            last = new Date();
        } else {
            last = todoFile.getLastDate();
        }
        return getDays(last) - getDays(first);
    }

    private void presentProject() throws Exception {
        presentProjectText();
        presentProjectData();
    }
    
    private void presentProjectText() throws Exception {
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            out.format("<pre id=\"%s\">", getTextId(todoFile));
            presentFile(todoFile.getFile());
            out.format("</pre>");
        }
    }
    
    private void presentProjectData() throws Exception {
        JsonObject spmData = buildSpmData();
        out.println("<script type='text/javascript'>");
        out.println("var spmData = " + spmData.toJson() + ";");
        out.println("</script>");
    }
    
    private JsonObject buildSpmData() throws Exception {
        JsonObject spmData = new JsonObject();
        JsonArray entries = new JsonArray();
        Iterator<TodoFile> iter = project.getTodoFileIterator();
        while (iter.hasNext()) {
            TodoFile todoFile = iter.next();
            entries.append(buildEntry(todoFile));
        }
        spmData.put("entries", entries);
        spmData.put("projectName", project.getName());
        return spmData;
    }
    
    private JsonValue buildEntry(TodoFile todoFile) throws Exception {
        JsonObject entry = new JsonObject();
        entry.put("id", todoFile.getId());
        entry.put("name", todoFile.getName());
        entry.put("finalCount", new JsonNumber(getFinalCount(todoFile)));
        entry.put("duration", new JsonNumber(getDuration(todoFile)));
        JsonArray todoCounts = new JsonArray();
        JsonArray todoPercents = new JsonArray();
        Iterator<DataPoint> iter = todoFile.getDataPointIterator();
        while (iter.hasNext()) {
            DataPoint point = iter.next();
            long time = point.getDate().getTime() - project.getBaseTime();
            JsonNumber day = new JsonNumber(1.0 * time / 1000.0 / 60.0 / 60.0 / 24.0);
            int count = point.getCount("TODO");
            int total = point.getTotalCount();
            
            JsonArray countPoint = new JsonArray()
                .append(day)
                .append(new JsonNumber(count));
            JsonArray percentPoint = new JsonArray()
                .append(day)
                .append(new JsonNumber(1.0 * count / total));
            todoCounts.append(countPoint);
            todoPercents.append(percentPoint);
        }
        entry.put("todoCounts", todoCounts);
        entry.put("todoPercents", todoPercents);
        return entry;
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
