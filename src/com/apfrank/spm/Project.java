package com.apfrank.spm;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.lib.Ref;


import java.io.File;
import java.io.FilenameFilter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.Date;

/**
 * A Project object provides an interface to access information about
 * an SPM Git Directory. This does not know about stories, backlog, etc.
 * We defer that to higher level logic.
 */
public class Project {

    private Git git;
    private String branch;
    private Path projectPath;
    private String projectName;
    
    private File repositoryDir;
    private File projectDir;

    private SymbolFilter symbolFilter;
    
    /**
     * Map filename -> TodoFile
     */
    private Map<String,TodoFile> todoFileMap;

    private CommitLog commitLog;
    
    private LinkedList<DataPoint> aggregatedDataPointList;
    
    public Project(Git git, String branch, Path path,
                   FilenameFilter filenameFilter,
                   SymbolFilter symbolFilter)
        throws Exception
    {
        this.git = git;
        this.branch = branch;
        this.projectPath = path;
        this.symbolFilter = symbolFilter;
        repositoryDir = GitTools.getRepositoryDir(git);
        projectDir = projectPath.getFile(repositoryDir);
        projectName = projectPath.toString();
        buildTodoFileMap(filenameFilter);
        buildCommitLog();
        performCounting();
        checkoutLatest();
        buildAggregatedDataPointList();
    }
    
    public File getRepositoryDir() {
        return repositoryDir;
    }
    
    public File getProjectDir() {
        return projectDir;
    }
    
    public String getName() {
        return projectName;
    }
    
    public String getId() throws Exception {
        return HashTool.getMd5(projectName);
    }
    
    public void setName(String name) {
        projectName = name;
    }
    
    public SymbolFilter getSymbolFilter() {
        return symbolFilter;
    }
    
    public int getFileCount() {
        return todoFileMap.size();
    }
    
    /**
     * Get time of the first commit.
     */
    public long getBaseTime() {
        return commitLog.getFirstDate().getTime();
    }
    
    /**
     * Get an Iterator over the filenames accepted by the provided
     * FilenameFilter.
     */
    public Iterator<String> getFilenameIterator() {
        return todoFileMap.keySet().iterator();
    }

    public Iterator<TodoFile> getTodoFileIterator() {
        return todoFileMap.values().iterator();
    }
    
    /**
     * @return Iterator over aggregated data points.
     */
    public Iterator<DataPoint> getAggregatedDataPointIterator() {
        return aggregatedDataPointList.iterator();
    }
    
    /**
     * Build todoFileMap, which maps:
     *
     * <pre>
     * String filename -> TodoFile
     * </pre>
     */
    private void buildTodoFileMap(FilenameFilter filenameFilter) {
        assert projectDir != null;
        assert todoFileMap == null;
        todoFileMap = new TreeMap<String,TodoFile>();
        SortedSet<String> set = FileTools.findFilenames(
            projectDir, filenameFilter);
        Iterator<String> iter = set.iterator();
        while (iter.hasNext()) {
            String filename = iter.next();
            Path path = new Path(projectPath, filename);
            TodoFile todoFile = new TodoFile(this, path);
            todoFileMap.put(filename, todoFile);
        }
    }

    private void buildCommitLog() throws Exception {
        assert todoFileMap != null;
        assert commitLog == null;
        commitLog = new CommitLog();
        Iterator<TodoFile> iter = getTodoFileIterator();
        while (iter.hasNext()) {
            addCommitsFromTodoFile(iter.next());
        }
    }
    
    private void addCommitsFromTodoFile(TodoFile todoFile) throws Exception {
        LogCommand log = git.log();
        log.add(git.getRepository().resolve(
            "refs/remotes/origin/" + branch));
        log.addPath(todoFile.getPath().toString());
        Iterator<RevCommit> iter = log.call().iterator();
        while (iter.hasNext()) {
            RevCommit revCommit = iter.next();
            Commit commit = commitLog.lookupCommit(revCommit);
            DataPoint dataPoint = new DataPoint(
                commit.getDate(),
                todoFile.getPath()
            );
            commit.addDataPoint(dataPoint);
            todoFile.addDataPoint(dataPoint);
        }
    }
    
    private void performCounting() throws Exception {
        Iterator<Commit> iter = commitLog.getCommitIterator();
        while (iter.hasNext()) {
            Commit commit = iter.next();
            performCountingOnCommit(commit);
        }
    }
    
    private void performCountingOnCommit(Commit commit)
        throws Exception
    {
        // git checkout commit.getHash();
        CheckoutCommand co = git.checkout();
        co.setStartPoint(commit.getRevCommit());
        co.setCreateBranch(false);
        co.setForce(false);
        // This is an anomaly in Jgit. We have to name the commits
        // we checkout headless is not allowed. So we just use the
        // hash.
        co.setName(commit.getHash());
        Ref ref = co.call();
        // TODO: what's ref for?
        Iterator<DataPoint> iter = commit.getDataPointIterator();
        while (iter.hasNext()) {
            DataPoint dataPoint = iter.next();
            performCountingOnDataPoint(dataPoint);
        }
    }
    
    private void performCountingOnDataPoint(DataPoint dataPoint)
        throws Exception
    {
        Path path = dataPoint.getPath();
        File file = path.getFile(repositoryDir);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        while (null != (line = reader.readLine())) {
            String symbol = symbolFilter.getSymbol(line);
            if (symbol != null) {
                dataPoint.increment(symbol);
            }
        }
    }
    
    private void checkoutLatest() throws Exception {
        CheckoutCommand co = git.checkout();
        co.setName(branch);
        Ref ref = co.call();
    }
    
    private void buildAggregatedDataPointList() {
        aggregatedDataPointList = new LinkedList<DataPoint>();
        long lowerTime = commitLog.getFirstDate().getTime();
        long upperTime = commitLog.getLastDate().getTime();
        final long aDay = 1000 * 60 * 60 * 24;
        for (long t = lowerTime; t <= upperTime; t += aDay) {
            Date sampleDate = new Date(t);
            DataPoint dataPoint = new DataPoint(sampleDate, projectPath);
            Iterator<TodoFile> iter = getTodoFileIterator();
            while (iter.hasNext()) {
                TodoFile todoFile = iter.next();
                DataPoint filePoint =
                    todoFile.getDataPointAtOrBefore(sampleDate);
                if (filePoint == null) {
                    continue;
                }
                Iterator<String> symbolIter = filePoint.getSymbolIterator();
                while (symbolIter.hasNext()) {
                    String symbol = symbolIter.next();
                    dataPoint.increment(symbol, filePoint.getCount(symbol));
                }
            }
            aggregatedDataPointList.add(dataPoint);
        }
    }
}
