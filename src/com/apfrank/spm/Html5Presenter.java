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
    
    private class Entry {
        private final long HOUR = 1000 * 60 * 60;
        private final long DAY = 24 * HOUR;
        private DataSource source;
        public Entry(DataSource source) {
            this.source = source;
        }
        public String getName() {
            return source.getName();
        }
        public String getAnchorId() {
            return "anchor" + source.getId();
        }
        public JsonObject getJsonObject(long baseTime) {
            JsonObject entry = new JsonObject();
            entry.put("id", new JsonString(source.getId()));
            entry.put("name", new JsonString(source.getName()));

            DateGenerator generator;
            long t0, t1;
            t0 = source.getFirstDate().getTime();
            t1 = source.getLastDate().getTime();
            if ((t1-t0) <= 2*DAY) {
                generator = DateGenerator.createHourly(
                    source.getFirstDate(),
                    source.getLastDate());
            } else {
                generator = DateGenerator.createDaily(
                    source.getFirstDate(),
                    source.getLastDate(),
                    8);
            }
            // TODO: todoPercents should really be named donePercents.
            //       but spm.js expects 'todoPercents'
            JsonArray todoCounts = new JsonArray();
            JsonArray todoPercents = new JsonArray();
            while (generator.hasNext()) {
                Date d = generator.next();
                int todo = source.getTodoCount(d);
                int done = source.getDoneCount(d);
                int total = source.getTotalCount(d);
                double donePercent = (double)done / (double)total;
                double day = (double)(d.getTime() - baseTime) / DAY;
                todoCounts.append((new JsonArray())
                                  .append(new JsonNumber(day))
                                  .append(new JsonNumber(todo)));
                todoPercents.append((new JsonArray())
                                    .append(new JsonNumber(day))
                                    .append(new JsonNumber(donePercent)));
            }
            entry.put("todoCounts", todoCounts);
            entry.put("todoPercents", todoPercents);
            entry.put("finalCount", finalCount());
            entry.put("finalTotal", finalTotal());
            entry.put("duration", duration());
            return entry;
        }
        
        private JsonNumber finalCount() {
            return new JsonNumber(source.getTodoCount(source.getLastDate()));
        }
        
        private JsonNumber finalTotal() {
            return new JsonNumber(source.getTotalCount(source.getLastDate()));
        }
        
        private JsonNumber duration() {
            double t0 = source.getFirstDate().getTime();
            double t1 = source.getLastDate().getTime();
            if (0 < source.getTodoCount(source.getLastDate())) {
                t1 = (new Date()).getTime();
            }
            double duration = (t1-t0) / DAY;
            return new JsonNumber(duration);
        }
    }
    
    private Project project;
    private PrintStream out;
    
    /**
     * An entry for the whole project.
     */
    private Entry projectEntry;
    
    /**
     * Entries from TodoFiles.
     */
    private LinkedList<Entry> todoEntries;
    
    public Html5Presenter(Project project) {
        this.project = project;
        this.out = System.out;
        this.projectEntry = new Entry(project.getAggregatedSource());
        this.todoEntries = new LinkedList<Entry>();
        Iterator<TodoFile> todoIter = project.getTodoFileIterator();
        while (todoIter.hasNext()) {
            todoEntries.add(new Entry(todoIter.next()));
        }
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
    
    /**
     * Build the SPM Data object.
     */
    private JsonObject buildSpmData() throws Exception {
        JsonObject spmData = new JsonObject();
        JsonArray entries = new JsonArray();
        // TODO: When Entry is ready, swap the following calls.
        if (false) {        
            entries.append(buildAggregateEntry());
            Iterator<TodoFile> iter = project.getTodoFileIterator();
            while (iter.hasNext()) {
                TodoFile todoFile = iter.next();
                entries.append(buildTodoFileEntry(todoFile));
            }
        } else {
            long baseTime = project.getBaseTime();
            entries.append(projectEntry.getJsonObject(baseTime));
            Iterator<Entry> iter = todoEntries.iterator();
            while (iter.hasNext()) {
                entries.append(iter.next().getJsonObject(baseTime));
            }
        }
        spmData.put("entries", entries);
        spmData.put("projectName", project.getName());
        return spmData;
    }
    
    private JsonValue buildTodoFileEntry(TodoFile todoFile) throws Exception {
        return buildEntry(todoFile.getId(),
                          todoFile.getName(),
                          todoFile.getDataPointIterator());
    }
    
    /**
     * Builds a special entry based on aggregated data.
     */
    private JsonValue buildAggregateEntry() throws Exception {
        return buildEntry(project.getId(),
                          "Project",
                          project.getAggregatedDataPointIterator());
    }
    
    private JsonValue buildEntry(String id,
                                 String name,
                                 Iterator<DataPoint> iter)
    {
        JsonObject entry = new JsonObject();
        entry.put("id", id);
        entry.put("name", name);
        JsonArray todoCounts = new JsonArray();
        JsonArray todoPercents = new JsonArray();
        DataPoint firstDataPoint = null;
        DataPoint lastDataPoint = null;
        while (iter.hasNext()) {
            DataPoint point = iter.next();
            if (firstDataPoint == null) {
                firstDataPoint = point;
            }
            lastDataPoint = point;
            
            long time = point.getDate().getTime() - project.getBaseTime();
            JsonNumber day = new JsonNumber(1.0 * time / 1000.0 / 60.0 / 60.0 / 24.0);
            int count = point.getCount("TODO");
            int total = point.getTotalCount();

            JsonArray countPoint = new JsonArray()
                .append(day)
                .append(new JsonNumber(count));
            JsonArray percentPoint = new JsonArray()
                .append(day)
                .append(new JsonNumber(1.0 * (total - count) / total));
            
            todoCounts.append(countPoint);
            todoPercents.append(percentPoint);
        }
        entry.put("todoCounts", todoCounts);
        entry.put("todoPercents", todoPercents);

        int finalCount = 0;
        int finalTotal = 0;
        double duration = 0.0;
        if (lastDataPoint != null) {
            finalCount = lastDataPoint.getCount("TODO");
            finalTotal = lastDataPoint.getTotalCount();
            double t0, t1;
            t0 = firstDataPoint.getDate().getTime();
            if (finalCount > 0) {
                t1 = new Date().getTime();
            } else {
                t1 = lastDataPoint.getDate().getTime();
            }
            duration = (t1-t0) / 1000 / 60 /60 / 24;
        }
        entry.put("finalCount", new JsonNumber(finalCount));
        entry.put("finalTotal", new JsonNumber(finalTotal));
        entry.put("duration", new JsonNumber(duration));

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
