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
    private int checkoutCount = 0;
    private String currentCommitHash = null;
    
    private File repositoryDir;
    private File projectDir;

    private SymbolFilter symbolFilter;
    
    /**
     * Map filename -> TodoFile. Use a map so files are ordered by path.
     */
    private TreeMap<String,TodoFile> todoFileMap;

    private CommitLog DELETE_commitLog;
    
    private LinkedList<DataPoint> aggregatedDataPointList;
    private AggregatedSource aggregatedSource;
    
    public Project(Git git, String branch, Path path,
                   FilenameFilter filenameFilter,
                   SymbolFilter symbolFilter)
        throws Exception
    {
        this.git = git;
        this.branch = branch;
        this.projectPath = path;
        this.symbolFilter = symbolFilter;
        this.checkoutCount = 0;
        repositoryDir = GitTools.getRepositoryDir(git);
        projectDir = projectPath.getFile(repositoryDir);
        projectName = projectPath.toString();
        todoFileMap = new TreeMap<String,TodoFile>();
        populateTodoFileMap(filenameFilter);
        System.err.println("FROG: checkoutCount in Project: " + checkoutCount);
    }
    
    public int getCheckoutCount() {
        return checkoutCount;
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
    
    public String getId() {
        return HashTool.getMd5(projectName);
    }
    
    public void setName(String name) {
        projectName = name;
    }
    
    public SymbolFilter getSymbolFilter() {
        return symbolFilter;
    }
    
    public DataSource getAggregatedSource() {
        if (aggregatedSource == null) {
            aggregatedSource = new AggregatedSource(getId(), getName());
            Iterator<TodoFile> iter = getTodoFileIterator();
            while (iter.hasNext()) {
                TodoFile f = iter.next();
                aggregatedSource.addSource(f);
            }
        }
        return aggregatedSource;
    }

    public int getFileCount() {
        return todoFileMap.size();
    }
    
    /**
     * Get time of the first commit.
     */
    public long getBaseTime() {
        return getAggregatedSource().getFirstDate().getTime();
    }
    
    /**
     * Get an Iterator over the filenames accepted by the provided
     * FilenameFilter.
     */
    public Iterator<String> DELETE_getFilenameIterator() {
        return todoFileMap.keySet().iterator();
    }

    public Iterator<TodoFile> getTodoFileIterator() {
        return todoFileMap.values().iterator();
    }
    
    /**
     * @return Iterator over aggregated data points.
     */
    // TODO: DELETE
    public Iterator<DataPoint> DELETE_getAggregatedDataPointIterator() {
        return aggregatedDataPointList.iterator();
    }
    
    /**
     * Get commits of a given path that are reachable from the project branch.
     */
    public Iterable<Commit> getCommits(Path path) {
        try {
            LinkedList<Commit> list = new LinkedList<Commit>();
            LogCommand log = git.log();
            log.add(git.getRepository().resolve("refs/remotes/origin/"+branch));
            log.addPath(path.toString());
            Iterator<RevCommit> iter = log.call().iterator();
            while (iter.hasNext()) {
                list.add(new Commit(iter.next()));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Build todoFileMap, which maps:
     *
     * <pre>
     * String filename -> TodoFile
     * </pre>
     */
    private void populateTodoFileMap(FilenameFilter filenameFilter) {
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
    
    /**
     * Create DataPoint for 'path' at 'commit'.
     */
    public DataPoint createDataPoint(Path path, Commit commit) {
        try {
            DataPoint dataPoint = new DataPoint(commit.getDate(), path);
            BufferedReader reader = new BufferedReader(openPathAtCommit(path, commit));
            String line;
            while (null != (line = reader.readLine())) {
                String symbol = symbolFilter.getSymbol(line);
                if (symbol != null) {
                    dataPoint.increment(symbol);
                }
            }
            return dataPoint;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * Open a FileReader for path at commit.  If commit is null, then
     * open path at the latest commit.
     */
    public FileReader openPathAtCommit(Path path, Commit commit) {
        try {
            checkoutCommit(commit);
            File file = path.getFile(repositoryDir);
            return new FileReader(file);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    /**
     * @param commit Commit to checkout. If null, checkout latest.
     */
    public void checkoutCommit(Commit commit) {
        try {
            if (commit == null && currentCommitHash == null) {
                // do nothing, already at latest commit.
            } else if (commit != null &&
                       commit.getHash().equals(currentCommitHash))
            {
                // do nothing, already at request commit.
            } else if (commit == null) {
                // Checkout latest.
                CheckoutCommand co = git.checkout();
                co.setName(branch);
                co.call();
                this.currentCommitHash = null;
                this.checkoutCount += 1;
            } else {
                // Checkout commit.
                CheckoutCommand co = git.checkout();
                co.setStartPoint(commit.getRevCommit());
                co.setCreateBranch(false);
                co.setForce(false);
                System.err.println("FROG checkout commit " + commit.getHash());
                co.setName(commit.getHash());
                Ref ref = co.call();
                this.currentCommitHash = commit.getHash();
                this.checkoutCount += 1;
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
